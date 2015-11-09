package com.ixxus.sar.action.constraint;

import com.ixxus.sar.model.SARContentModel;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Ixxus on 11/9/2015.
 */
public class SetAssociationRuleParameterConstraintTest {

    private DictionaryService dictionaryService;
    private Collection<QName> allAssociations;

    @Before
    public void setUp() throws Exception {

        dictionaryService = mock(DictionaryService.class);
        allAssociations = new ArrayList<>();

        when(dictionaryService.getAllAssociations()).thenReturn(allAssociations);
    }

    @Test
    public void testGetAllowableValuesImpl() throws Exception {
        SARContentModel.SARClass relatedToContent = new SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent();
        SARContentModel.SARClass relatedToFolder = new SARContentModel.Aspect.RelatedTo.Assoc.RelatedToFolder();
        setupMockForAssoc(relatedToContent);
        setupMockForAssoc(relatedToFolder);

        SetAssociationRuleParameterConstraint setAssociationRuleParameterConstraint
                = new SetAssociationRuleParameterConstraint();
        setAssociationRuleParameterConstraint.setDictionaryService(dictionaryService);
        setAssociationRuleParameterConstraint.setNamespaceUris(Arrays.asList(SARContentModel.NAMESPACE_URI));

        Map<String, String> allowedValuesImpl = setAssociationRuleParameterConstraint.getAllowableValuesImpl();

        assertThat(allowedValuesImpl).contains(getMapEntryFromAssociation(relatedToContent));
        assertThat(allowedValuesImpl).contains(getMapEntryFromAssociation(relatedToFolder));
    }

    protected MapEntry getMapEntryFromAssociation(SARContentModel.SARClass association) {
        return MapEntry.entry(association.QNAME.toPrefixString() + "|" + association.TARGET_TYPE.getPrefixString(),
                association.TITLE);
    }

    protected void setupMockForAssoc(SARContentModel.SARClass association) {
        allAssociations.add(association.QNAME);

        ClassDefinition targetClassDef = mock(ClassDefinition.class);
        AssociationDefinition assocDef = mock(AssociationDefinition.class);
        when(dictionaryService.getAssociation(association.QNAME)).thenReturn(assocDef);
        when(assocDef.getTitle(dictionaryService)).thenReturn(association.TITLE);

        when(assocDef.getTargetClass()).thenReturn(targetClassDef);
        when(targetClassDef.getName()).thenReturn(association.TARGET_TYPE);
    }
}