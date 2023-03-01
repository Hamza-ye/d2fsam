/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.email;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.i18n.I18nManager;
import org.nmcpye.am.i18n.locale.LocaleManager;
import org.nmcpye.am.message.MessageSender;
import org.nmcpye.am.outboundmessage.OutboundMessageResponse;
import org.nmcpye.am.setting.SettingKey;
import org.nmcpye.am.setting.SystemSettingManager;
import org.nmcpye.am.system.template.TemplateManager;
import org.nmcpye.am.system.util.ValidationUtils;
import org.nmcpye.am.user.CurrentUserService;
import org.nmcpye.am.user.User;
import org.nmcpye.am.user.UserSettingKey;
import org.nmcpye.am.user.UserSettingService;
import org.nmcpye.am.util.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Halvdan Hoem Grelland <halvdanhg@gmail.com>
 */
@Transactional // TODO do we need transactions at all here?
@Service("org.nmcpye.am.email.EmailService")
@Slf4j
public class DefaultEmailService
    implements EmailService {

    private static final String TEST_EMAIL_SUBJECT = "Test email from Field-AM-System";

    private static final String TEST_EMAIL_TEXT = "This is an automatically generated email from ";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private final MessageSender emailMessageSender;

    private final CurrentUserService currentUserService;

    private final SystemSettingManager systemSettingManager;

    // NMCP
    private final AmConfigurationProvider configurationProvider;
    private final UserSettingService userSettingService;
    private final I18nManager i18nManager;

    private static final String USER = "user";
    ///////////////////////

    public DefaultEmailService(MessageSender emailMessageSender, CurrentUserService currentUserService,
                               SystemSettingManager systemSettingManager,
                               AmConfigurationProvider configurationProvider,
                               UserSettingService userSettingService, I18nManager i18nManager) {
        checkNotNull(emailMessageSender);
        checkNotNull(currentUserService);
        checkNotNull(emailMessageSender);
        checkNotNull(userSettingService);
        checkNotNull(i18nManager);
        checkNotNull(configurationProvider);

        this.emailMessageSender = emailMessageSender;
        this.currentUserService = currentUserService;
        this.systemSettingManager = systemSettingManager;
        this.configurationProvider = configurationProvider;
        this.userSettingService = userSettingService;
        this.i18nManager = i18nManager;
    }

    // -------------------------------------------------------------------------
    // EmailService implementation
    // -------------------------------------------------------------------------

    @Override
    public boolean emailConfigured() {
        return systemSettingManager.emailConfigured();
    }

    @Override
    public OutboundMessageResponse sendEmail(Email email) {
        return emailMessageSender.sendMessage(email.getSubject(), email.getText(), null, email.getSender(),
            email.getRecipients(), true);
    }

    @Override
    public OutboundMessageResponse sendEmail(String subject, String message, Set<String> recipients) {
        return emailMessageSender.sendMessage(subject, message, recipients);
    }

    @Override
    public OutboundMessageResponse sendTestEmail() {
        String instanceName = systemSettingManager.getStringSetting(SettingKey.APPLICATION_TITLE);

        Email email = new Email(TEST_EMAIL_SUBJECT, TEST_EMAIL_TEXT + instanceName, null,
            Sets.newHashSet(currentUserService.getCurrentUser()));

        return sendEmail(email);
    }

    @Override
    public OutboundMessageResponse sendSystemEmail(Email email) {
        OutboundMessageResponse response = new OutboundMessageResponse();

        String recipient = systemSettingManager.getStringSetting(SettingKey.SYSTEM_NOTIFICATIONS_EMAIL);
        String appTitle = systemSettingManager.getStringSetting(SettingKey.APPLICATION_TITLE);

        if (recipient == null || !ValidationUtils.emailIsValid(recipient)) {
            response.setOk(false);
            response.setDescription("No recipient found");

            return response;
        }

        User user = new User();
        user.setEmail(recipient);

        User sender = new User();
        sender.setFirstName(StringUtils.trimToEmpty(appTitle));
        sender.setSurname(recipient);

        return emailMessageSender.sendMessage(email.getSubject(), email.getText(), null, sender,
            Sets.newHashSet(user), true);
    }

    // NMCP //////////////
    @Async
    @Override
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        String baseUrl = configurationProvider.getServerBaseUrl();

        HashMap<String, Object> values = new HashMap<>(2);

//        Locale locale = Locale.forLanguageTag(user.getLangKey());
//        locale = ObjectUtils.firstNonNull(locale, LocaleManager.DEFAULT_LOCALE);

        Locale locale = (Locale) userSettingService.getUserSetting(UserSettingKey.UI_LOCALE,
            user);
        locale = ObjectUtils.firstNonNull(locale, LocaleManager.DEFAULT_LOCALE);

        values.put("baseUrl", baseUrl);
        values.put("subject", titleKey);
        values.put("user", user);
        values.put("i18n", i18nManager.getI18n(locale));

        String content = new TemplateManager().render(values, templateName);
        sendEmail(titleKey, content, Set.of(user.getEmail()));
    }
    //////////////////////
}
