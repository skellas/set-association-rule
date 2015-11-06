package com.ixxus.sar.action.constraint;

import org.alfresco.repo.action.constraint.BaseParameterConstraint;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;

import java.util.*;

/**
 * Created by Ixxus on 11/6/2015.
 */
public class SetAssociationRuleParameterConstraint extends BaseParameterConstraint{
    public static final String NAME = "set-association-rule";

    private DictionaryService dictionaryService;
    private List<String> namespaceUris;

    @Override protected Map<String, String> getAllowableValuesImpl() {
        Collection associations = this.dictionaryService.getAllAssociations();
        Map result = new HashMap(associations.size());
        Iterator assocIterator = associations.iterator();

        while(assocIterator.hasNext()) {
            QName associationQName = (QName)assocIterator.next();
            if (!namespaceUris.contains(associationQName.getNamespaceURI()))
                continue;
            AssociationDefinition associationDef = this.dictionaryService.getAssociation(associationQName);
            if(associationDef != null && associationDef.getTitle(this.dictionaryService) != null) {
                result.put(associationQName.toPrefixString()+"|"+associationDef.getTargetClass().getName().toPrefixString(),
                        associationDef.getTitle(this.dictionaryService));
            }
        }

        return result;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public List<String> getNamespaceUris() {
        return namespaceUris;
    }

    public void setNamespaceUris(List<String> namespaceUris) {
        this.namespaceUris = namespaceUris;
    }
}
