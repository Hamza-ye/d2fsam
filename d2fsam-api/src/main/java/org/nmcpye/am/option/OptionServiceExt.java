package org.nmcpye.am.option;

import org.nmcpye.am.common.IllegalQueryException;
import org.nmcpye.am.feedback.ErrorMessage;

import java.util.List;

/**
 * Service Interface for managing {@link Option}.
 */
public interface OptionServiceExt {
    String ID = OptionServiceExt.class.getName();

    // -------------------------------------------------------------------------
    // OptionSet
    // -------------------------------------------------------------------------

    long saveOptionSet(OptionSet optionSet);

    void updateOptionSet(OptionSet optionSet);

    /**
     * Validate an {@link OptionSet}.
     *
     * @param optionSet the set to validate
     * @throws IllegalQueryException when the provided {@link OptionSet} has
     *                               validation errors
     */
    void validateOptionSet(OptionSet optionSet) throws IllegalQueryException;

    ErrorMessage validateOption(OptionSet optionSet, Option option);

    OptionSet getOptionSet(Long id);

    OptionSet getOptionSet(String uid);

    OptionSet getOptionSetByName(String name);

    OptionSet getOptionSetByCode(String code);

    void deleteOptionSet(OptionSet optionSet);

    List<OptionSet> getAllOptionSets();

    List<Option> getOptions(Long optionSetId, String name, Integer max);

    // -------------------------------------------------------------------------
    // Option
    // -------------------------------------------------------------------------

    void updateOption(Option option);

    Option getOption(Long id);

    Option getOptionByCode(String code);

    void deleteOption(Option option);

    // -------------------------------------------------------------------------
    // OptionGroup
    // -------------------------------------------------------------------------

    long saveOptionGroup(OptionGroup group);

    void updateOptionGroup(OptionGroup group);

    OptionGroup getOptionGroup(Long id);

    OptionGroup getOptionGroup(String uid);

    void deleteOptionGroup(OptionGroup group);

    List<OptionGroup> getAllOptionGroups();

    // -------------------------------------------------------------------------
    // OptionGroupSet
    // -------------------------------------------------------------------------

    Long saveOptionGroupSet(OptionGroupSet group);

    void updateOptionGroupSet(OptionGroupSet group);

    OptionGroupSet getOptionGroupSet(Long id);

    OptionGroupSet getOptionGroupSet(String uid);

    void deleteOptionGroupSet(OptionGroupSet group);

    List<OptionGroupSet> getAllOptionGroupSets();
}
