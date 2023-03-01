package org.nmcpye.am.option;

import org.nmcpye.am.common.IdentifiableObjectStore;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.feedback.ErrorCode;
import org.nmcpye.am.feedback.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service Implementation for managing {@link Option}.
 */
@Service("org.nmcpye.am.option.OptionServiceExt")
public class OptionServiceExtImpl implements OptionServiceExt {

    private final Logger log = LoggerFactory.getLogger(OptionServiceExtImpl.class);

    private final OptionRepositoryExt optionRepositoryExt;

    private IdentifiableObjectStore<OptionSet> optionSetStore;

    private OptionGroupRepositoryExt optionGroupStore;

    private OptionGroupSetRepositoryExt optionGroupSetStore;

    public OptionServiceExtImpl(
        @Qualifier("org.nmcpye.am.option.OptionSetStore")
            IdentifiableObjectStore<OptionSet> optionSetStore,
        OptionRepositoryExt optionRepositoryExt,
        OptionGroupRepositoryExt optionGroupStore, OptionGroupSetRepositoryExt optionGroupSetStore) {
        this.optionRepositoryExt = optionRepositoryExt;
        this.optionSetStore = optionSetStore;
        this.optionGroupStore = optionGroupStore;
        this.optionGroupSetStore = optionGroupSetStore;
    }

    @Override
    @Transactional
    public long saveOptionSet(OptionSet optionSet) {
        validateOptionSet(optionSet);
        optionSetStore.saveObject(optionSet);
        return optionSet.getId();
    }

    @Override
    @Transactional
    public void updateOptionSet(OptionSet optionSet) {
        validateOptionSet(optionSet);
        optionSetStore.update(optionSet);
    }

    @Override
    public void validateOptionSet(OptionSet optionSet) throws IllegalQueryException {
        if (optionSet.getValueType() != ValueType.MULTI_TEXT) {
            return;
        }
        for (Option option : optionSet.getOptions()) {
            if (option.getId() != null && option.getId() != 0L && option.getCode() == null) {
                option = optionRepositoryExt.get(option.getId());
            }
            ErrorMessage error = validateOption(optionSet, option);
            if (error != null) {
                throw new IllegalQueryException(error);
            }
        }
    }

    @Override
    public ErrorMessage validateOption(OptionSet optionSet, Option option) {
        if (optionSet != null &&
            optionSet.getValueType() == ValueType.MULTI_TEXT &&
            option.getCode().contains(ValueType.MULTI_TEXT_SEPARATOR)) {
            return new ErrorMessage(ErrorCode.E1118, optionSet.getUid(), option.getCode());
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public OptionSet getOptionSet(Long id) {
        return optionSetStore.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionSet getOptionSet(String uid) {
        return optionSetStore.getByUid(uid);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionSet getOptionSetByName(String name) {
        return optionSetStore.getByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionSet getOptionSetByCode(String code) {
        return optionSetStore.getByCode(code);
    }

    @Override
    @Transactional
    public void deleteOptionSet(OptionSet optionSet) {
        optionSetStore.deleteObject(optionSet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionSet> getAllOptionSets() {
        return optionSetStore.getAll();
    }

    // -------------------------------------------------------------------------
    // Option
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Option> getOptions(Long optionSetId, String key, Integer max) {
        List<Option> options;

        if (key != null || max != null) {
            // Use query as option set size might be very high

            options = optionRepositoryExt.getOptions(optionSetId, key, max);
        } else {
            // Return all from object association to preserve custom order

            OptionSet optionSet = getOptionSet(optionSetId);

            options = new ArrayList<>(optionSet.getOptions());
        }

        return options;
    }

    @Override
    @Transactional
    public void updateOption(Option option) {
        optionRepositoryExt.update(option);
    }

    @Override
    @Transactional(readOnly = true)
    public Option getOption(Long id) {
        return optionRepositoryExt.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Option getOptionByCode(String code) {
        return optionRepositoryExt.getByCode(code);
    }

    @Override
    @Transactional
    public void deleteOption(Option option) {
        optionRepositoryExt.deleteObject(option);
    }

    // -------------------------------------------------------------------------
    // OptionGroup
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public long saveOptionGroup(OptionGroup group) {
        optionGroupStore.saveObject(group);

        return group.getId();
    }

    @Override
    @Transactional
    public void updateOptionGroup(OptionGroup group) {
        optionGroupStore.update(group);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionGroup getOptionGroup(Long id) {
        return optionGroupStore.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionGroup getOptionGroup(String uid) {
        return optionGroupStore.getByUid(uid);
    }

    @Override
    @Transactional
    public void deleteOptionGroup(OptionGroup group) {
        optionGroupStore.deleteObject(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionGroup> getAllOptionGroups() {
        return optionGroupStore.getAll();
    }

    // -------------------------------------------------------------------------
    // OptionGroupSet
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Long saveOptionGroupSet(OptionGroupSet group) {
        optionGroupSetStore.saveObject(group);

        return group.getId();
    }

    @Override
    @Transactional
    public void updateOptionGroupSet(OptionGroupSet group) {
        optionGroupSetStore.update(group);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionGroupSet getOptionGroupSet(Long id) {
        return optionGroupSetStore.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionGroupSet getOptionGroupSet(String uid) {
        return optionGroupSetStore.getByUid(uid);
    }

    @Override
    @Transactional
    public void deleteOptionGroupSet(OptionGroupSet group) {
        optionGroupSetStore.deleteObject(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptionGroupSet> getAllOptionGroupSets() {
        return optionGroupSetStore.getAll();
    }
}
