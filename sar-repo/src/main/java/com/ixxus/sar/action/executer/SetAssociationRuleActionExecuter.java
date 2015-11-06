package com.ixxus.sar.action.executer;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by Ixxus on 11/6/2015.
 */
public class SetAssociationRuleActionExecuter extends ActionExecuterAbstractBase {
    public static final String NAME = "set-association-rule";
    public static final String PARAM_ASSOCIATION_NAME = "association-name";
    public static final String PARAM_ASSOCIATION_VALUE = "association-value";

    private NodeService nodeService;
    private DictionaryService dictionaryService;

    private static final Log LOGGER = LogFactory.getLog(SetAssociationRuleActionExecuter.class);

    @Override protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {

        LOGGER.debug("Action applied to nodeRef " + actionedUponNodeRef.toString());
        if(this.nodeService.exists(actionedUponNodeRef)) {
            LOGGER.debug("parameters");
            LOGGER.debug(action.getParameterValues().toString());
            List<NodeRef> nodesToAssociate = (List<NodeRef>)action.getParameterValue(PARAM_ASSOCIATION_VALUE);
            QName associationName = (QName) action.getParameterValue(PARAM_ASSOCIATION_NAME);
            // check if the node type allows this particular association to be written
            // write the values into the defined association
            AssociationDefinition assocationDefinition = dictionaryService.getAssociation(associationName);
            QName sourceNodeType = assocationDefinition.getSourceClass().getName(),
                    targetNodeType = assocationDefinition.getTargetClass().getName();
            LOGGER.debug("Association : " + assocationDefinition.getName());
            LOGGER.debug("Source Class : " + sourceNodeType.toString());
            LOGGER.debug("Target Class : " + targetNodeType.toString());

            // we can't be sure if the sourceNodeType is an aspect or a content type
            // so we'll look for both and test later
            AspectDefinition sourceNodeAspectType = dictionaryService.getAspect(sourceNodeType);
            LOGGER.debug(null == sourceNodeAspectType ? "Source is not an aspect" : "Source is an aspect");
            TypeDefinition sourceNodeContentType = dictionaryService.getType(sourceNodeType);
            LOGGER.debug(null == sourceNodeContentType ? "Source is not a type" : "Source is a content type");


            /*  Test Case - Either, the aspectType is null  and the node is of valid contentType
                            Or, the contentType is null and the node has the valid aspectType
                            Else, log an error
                if (
                        (
                            (aspectType == null)
                            &&
                            !(sourceNode.type == contentType)
                        )
                        ||
                        (
                            (contentType == null)
                            &&
                            !(sourceNode.aspects.contains(aspectType))
                         )
                    )
             */
            if (
                    (
                            (
                                (sourceNodeAspectType == null)
                                &&
                                (nodeService.getType(actionedUponNodeRef).equals(sourceNodeContentType.getName()))
                            )
                            ||
                            (
                                (sourceNodeContentType == null)
                                &&
                                (nodeService.getAspects(actionedUponNodeRef).contains(sourceNodeAspectType.getName()))
                            )
                    )
            )
            {
                for (NodeRef nodeToAssociate : nodesToAssociate) {
                    if (nodeService.getType(nodeToAssociate).equals(targetNodeType)) {
                        LOGGER.debug("Creating Association to NodeRef :" + nodeToAssociate.toString());
                        nodeService.createAssociation(actionedUponNodeRef, nodeToAssociate, associationName);
                    } else {
                        LOGGER.warn("Target Node " + nodeToAssociate.toString() + " is not of the proper type for this rule");
                    }
                }
            } else {
                LOGGER.warn(actionedUponNodeRef + " does not qualify for this rule");
            }
        }
    }

    @Override protected void addParameterDefinitions(List<ParameterDefinition> paramList) {

    }
}
