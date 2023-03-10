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
package org.nmcpye.am.webapi.controller;

import com.fasterxml.jackson.core.JsonParseException;
import io.github.classgraph.ClassGraph;
import org.apache.commons.lang3.EnumUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.nmcpye.am.common.DeleteNotAllowedException;
import org.nmcpye.am.common.IdentifiableProperty;
import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.common.exception.InvalidIdentifierReferenceException;
import org.nmcpye.am.commons.jackson.jsonpatch.JsonPatchException;
import org.nmcpye.am.dxf2.metadata.MetadataExportException;
import org.nmcpye.am.dxf2.metadata.MetadataImportException;
import org.nmcpye.am.dxf2.webmessage.WebMessage;
import org.nmcpye.am.dxf2.webmessage.WebMessageException;
import org.nmcpye.am.feedback.Status;
import org.nmcpye.am.query.QueryException;
import org.nmcpye.am.query.QueryParserException;
import org.nmcpye.am.schema.SchemaPathException;
import org.nmcpye.am.util.DateUtils;
import org.nmcpye.am.webapi.controller.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import javax.servlet.ServletException;
import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static org.nmcpye.am.dxf2.webmessage.WebMessageUtils.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@ControllerAdvice(basePackages = "org.nmcpye.am.webapi.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CrudControllerAdvice {

    // Add sensitive exceptions into this array
    private static final Class<?>[] SENSITIVE_EXCEPTIONS = {
        BadSqlGrammarException.class,
        org.hibernate.QueryException.class,
        DataAccessResourceFailureException.class,
    };

    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error has occured. Please contact your system administrator";

    private final List<Class<?>> enumClasses;

    public CrudControllerAdvice() {
        this.enumClasses = new ClassGraph().acceptPackages("org.nmcpye.org")
            .enableClassInfo().scan().getAllClasses().getEnums().loadClasses();
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new FromTextPropertyEditor(DateUtils::parseDate));
        binder.registerCustomEditor(IdentifiableProperty.class, new FromTextPropertyEditor(String::toUpperCase));
        this.enumClasses.forEach(c -> binder.registerCustomEditor(c, new ConvertEnum(c)));
    }

    @ExceptionHandler(RestClientException.class)
    @ResponseBody
    public WebMessage restClientExceptionHandler(RestClientException ex) {
        return createWebMessage(ex.getMessage(), Status.ERROR, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IllegalQueryException.class)
    @ResponseBody
    public WebMessage illegalQueryExceptionHandler(IllegalQueryException ex) {
        return conflict(ex.getMessage(), ex.getErrorCode());
    }

    //    @ExceptionHandler( Dhis2ClientException.class )
    //    @ResponseBody
    //    public WebMessage dhis2ClientException( Dhis2ClientException ex )
    //    {
    //        return conflict( ex.getMessage(), ex.getErrorCode() );
    //    }

    //    @ExceptionHandler( QueryRuntimeException.class )
    //    @ResponseBody
    //    public WebMessage queryRuntimeExceptionHandler( QueryRuntimeException ex )
    //    {
    //        return conflict( ex.getMessage(), ex.getErrorCode() );
    //    }

    @ExceptionHandler(DeleteNotAllowedException.class)
    @ResponseBody
    public WebMessage deleteNotAllowedExceptionHandler(DeleteNotAllowedException ex) {
        return conflict(ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(InvalidIdentifierReferenceException.class)
    @ResponseBody
    public WebMessage invalidIdentifierReferenceExceptionHandler(InvalidIdentifierReferenceException ex) {
        return conflict(ex.getMessage());
    }

    //    @ExceptionHandler( { DataApprovalException.class, AdxException.class, IllegalStateException.class } )
    //    @ResponseBody
    //    public WebMessage dataApprovalExceptionHandler( Exception ex )
    //    {
    //        return conflict( ex.getMessage() );
    //    }

    @ExceptionHandler({JsonParseException.class, MetadataImportException.class, MetadataExportException.class})
    @ResponseBody
    public WebMessage jsonParseExceptionHandler(Exception ex) {
        return conflict(ex.getMessage());
    }

    @ExceptionHandler({QueryParserException.class, QueryException.class})
    @ResponseBody
    public WebMessage queryExceptionHandler(Exception ex) {
        return conflict(ex.getMessage());
    }

    //    @ExceptionHandler(FieldFilterException.class)
    //    @ResponseBody
    //    public WebMessage fieldFilterExceptionHandler(FieldFilterException ex) {
    //        return conflict(ex.getMessage());
    //    }

    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseBody
    public WebMessage notAuthenticatedExceptionHandler(NotAuthenticatedException ex) {
        return unauthorized(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public WebMessage notFoundExceptionHandler(NotFoundException ex) {
        return notFound(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public WebMessage constraintViolationExceptionHandler(ConstraintViolationException ex) {
        return error(getExceptionMessage(ex));
    }

    //    @ExceptionHandler( MaintenanceModeException.class )
    //    @ResponseBody
    //    public WebMessage maintenanceModeExceptionHandler( MaintenanceModeException ex )
    //    {
    //        return serviceUnavailable( ex.getMessage() );
    //    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public WebMessage accessDeniedExceptionHandler(AccessDeniedException ex) {
        return forbidden(ex.getMessage());
    }

    @ExceptionHandler(WebMessageException.class)
    @ResponseBody
    public WebMessage webMessageExceptionHandler(WebMessageException ex) {
        return ex.getWebMessage();
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    @ResponseBody
    public WebMessage httpStatusCodeExceptionHandler(HttpStatusCodeException ex) {
        return createWebMessage(ex.getMessage(), Status.ERROR, ex.getStatusCode());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public WebMessage httpClientErrorExceptionHandler(HttpClientErrorException ex) {
        return createWebMessage(ex.getMessage(), Status.ERROR, ex.getStatusCode());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseBody
    public WebMessage httpServerErrorExceptionHandler(HttpServerErrorException ex) {
        return createWebMessage(ex.getMessage(), Status.ERROR, ex.getStatusCode());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public WebMessage httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        return createWebMessage(ex.getMessage(), Status.ERROR, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public WebMessage httpMediaTypeNotAcceptableExceptionHandler(HttpMediaTypeNotAcceptableException ex) {
        return createWebMessage(ex.getMessage(), Status.ERROR, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public WebMessage httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException ex) {
        return createWebMessage(ex.getMessage(), Status.ERROR, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(ServletException.class)
    public void servletExceptionHandler(ServletException ex) throws ServletException {
        throw ex;
    }

    @ExceptionHandler({
        BadRequestException.class, IllegalArgumentException.class,
        SchemaPathException.class, JsonPatchException.class})
    @ResponseBody
    public WebMessage handleBadRequest(Exception exception) {
        return badRequest(exception.getMessage());
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseBody
    public WebMessage handleConflictRequest(Exception exception) {
        return conflict(exception.getMessage());
    }

    //    @ExceptionHandler( MetadataVersionException.class )
    //    @ResponseBody
    //    public WebMessage handleMetaDataVersionException( MetadataVersionException metadataVersionException )
    //    {
    //        return error( metadataVersionException.getMessage() );
    //    }

    //    @ExceptionHandler( MetadataSyncException.class )
    //    @ResponseBody
    //    public WebMessage handleMetaDataSyncException( MetadataSyncException metadataSyncException )
    //    {
    //        return error( metadataSyncException.getMessage() );
    //    }
    //
    //    @ExceptionHandler( DhisVersionMismatchException.class )
    //    @ResponseBody
    //    public WebMessage handleDhisVersionMismatchException( DhisVersionMismatchException versionMismatchException )
    //    {
    //        return forbidden( versionMismatchException.getMessage() );
    //    }
    //
    //    @ExceptionHandler( MetadataImportConflictException.class )
    //    @ResponseBody
    //    public WebMessage handleMetadataImportConflictException( MetadataImportConflictException conflictException )
    //    {
    //        if ( conflictException.getMetadataSyncSummary() == null )
    //        {
    //            return conflict( conflictException.getMessage() );
    //        }
    //        return conflict( null ).setResponse( conflictException.getMetadataSyncSummary() );
    //    }

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseBody
    public WebMessage handleOperationNotAllowedException(OperationNotAllowedException ex) {
        return forbidden(ex.getMessage());
    }

    //    @ExceptionHandler( OAuth2AuthenticationException.class )
    //    @ResponseBody
    //    public WebMessage handleOAuth2AuthenticationException( OAuth2AuthenticationException ex )
    //    {
    //        OAuth2Error error = ex.getError();
    //        if ( error instanceof BearerTokenError )
    //        {
    //            BearerTokenError bearerTokenError = (BearerTokenError) error;
    //            HttpStatus status = ((BearerTokenError) error).getHttpStatus();
    //
    //            return createWebMessage( bearerTokenError.getErrorCode(),
    //                bearerTokenError.getDescription(), Status.ERROR, status );
    //        }
    //        return unauthorized( ex.getMessage() );
    //    }
    //
    //    @ExceptionHandler( ApiTokenAuthenticationException.class )
    //    @ResponseBody
    //    public WebMessage handleApiTokenAuthenticationException( ApiTokenAuthenticationException ex )
    //    {
    //        ApiTokenError apiTokenError = ex.getError();
    //        if ( apiTokenError != null )
    //        {
    //            return createWebMessage( apiTokenError.getDescription(), Status.ERROR,
    //                apiTokenError.getHttpStatus() );
    //        }
    //        return unauthorized( ex.getMessage() );
    //    }

    //    @ExceptionHandler( { PotentialDuplicateConflictException.class } )
    //    @ResponseBody
    //    public WebMessage handlePotentialDuplicateConflictRequest( Exception exception )
    //    {
    //        return conflict( exception.getMessage() );
    //    }
    //
    //    @ExceptionHandler( { PotentialDuplicateForbiddenException.class } )
    //    @ResponseBody
    //    public WebMessage handlePotentialDuplicateForbiddenRequest( Exception exception )
    //    {
    //        return forbidden( exception.getMessage() );
    //    }

    /**
     * Catches default exception and send back to user, but re-throws internally
     * so it still ends up in server logs.
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public WebMessage defaultExceptionHandler(Exception ex) {
        ex.printStackTrace();
        return error(getExceptionMessage(ex));
    }

    private String getExceptionMessage(Exception ex) {
        boolean isMessageSensitive = false;

        String message = ex.getMessage();

        if (isSensitiveException(ex)) {
            isMessageSensitive = true;
        }

        if (ex.getCause() != null) {
            message = ex.getCause().getMessage();

            if (isSensitiveException(ex.getCause())) {
                isMessageSensitive = true;
            }
        }

        if (isMessageSensitive) {
            message = GENERIC_ERROR_MESSAGE;
        }
        return message;
    }

    private boolean isSensitiveException(Throwable e) {
        for (Class<?> exClass : SENSITIVE_EXCEPTIONS) {
            if (exClass.isAssignableFrom(e.getClass())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Simple adapter to {@link PropertyEditorSupport} that allows to use lambda
     * {@link Function}s to convert value from its text representation.
     */
    private static final class FromTextPropertyEditor extends PropertyEditorSupport {

        private final Function<String, Object> fromText;

        private FromTextPropertyEditor(Function<String, Object> fromText) {
            this.fromText = fromText;
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(fromText.apply(text));
        }
    }

    private static final class ConvertEnum<T extends Enum<T>> extends PropertyEditorSupport {
        private final Class<T> enumClass;

        private ConvertEnum(Class<T> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public void setAsText(String text)
            throws IllegalArgumentException {
            Enum<T> enumValue = EnumUtils.getEnum(enumClass, text.toUpperCase());

            setValue(enumValue != null ? enumValue : text);
        }
    }
}
