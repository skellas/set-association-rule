
/**
 * Ixxus root namespace.
 *
 * @namespace Ixxus
 */
// Ensure Ixxus root object exists
if (typeof Ixxus == "undefined" || !Ixxus)
{
    var Ixxus = {};
}
/**
 * RuleConfigAction.
 *
 * @namespace Ixxus
 * @class Ixxus.SARRuleConfigAction
 */
(function()
{

    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector,
        Event = YAHOO.util.Event;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $hasEventInterest = Alfresco.util.hasEventInterest;

    Ixxus.SARRuleConfigAction = function(htmlId)
    {
        Ixxus.SARRuleConfigAction.superclass.constructor.call(this, htmlId);

        // Re-register with our own name
        this.name = "Ixxus.SARRuleConfigAction";
        Alfresco.util.ComponentManager.reregister(this);

        // Instance variables
        this.customisations = YAHOO.lang.merge(this.customisations, Ixxus.SARRuleConfigAction.superclass.customisations);
        this.renderers = YAHOO.lang.merge(this.renderers, Ixxus.SARRuleConfigAction.superclass.renderers);
        return this;
    };

    YAHOO.extend(Ixxus.SARRuleConfigAction, Alfresco.RuleConfigAction,
        {

            /**
             * CUSTOMISATIONS
             */

            customisations:
            {

                SetAssocationRule:
                {
                    text: function(configDef, ruleConfig, configEl){
                        configDef.parameterDefinitions[0].type = "arca:set-association-rule-name";
                        configDef.parameterDefinitions[1]._type = "path";
                        return configDef;
                    },
                    edit: function(configDef, ruleConfig, configEl)
                    {

                        // hide the real "value" field, as it's just a text field and we're
                        // going to build a cool custom ui for it
                        configDef.parameterDefinitions[1]._type = "hidden";
                        configDef.parameterDefinitions.push(
                            {
                                type: "arca:set-association-rule-value",
                                namedValue: configDef.parameterDefinitions[1].name
                            });
                        return configDef;
                    }
                }

            },

            /**
             * RENDERERS
             */

            renderers: {

                "arca:set-association-rule-name": {
                    manual: {edit: true},
                    currentCtx: {},
                    text: function (containerEl, configDef, paramDef, ruleConfig, value) {

                        this.renderers["arca:set-association-rule-name"].currentCtx =
                        {
                            configDef: configDef,
                            ruleConfig: ruleConfig,
                            paramDef: paramDef
                        };

                        var constraintOptions = this._getConstraintValues(paramDef, ruleConfig);
                        console.log(constraintOptions);
                        for (var i = 0, l = constraintOptions.length; i < l; i++)
                        {
                            constraintOption = constraintOptions[i];
                            var labelValue = "" + constraintOption.value;
                            if (labelValue.indexOf(value) >= 0)
                            {
                                // found a label!
                                return this._createValueSpan(containerEl, configDef, paramDef, ruleConfig, constraintOption.displayLabel);
                            }
                        }
                    }
                },
                "arca:set-association-rule-value": {
                    manual: {edit: true},
                    currentCtx: {},
                    text: function (containerEl, configDef, paramDef, ruleConfig, value) {

                        this.renderers["arca:set-association-rule-value"].currentCtx =
                        {
                            configDef: configDef,
                            ruleConfig: ruleConfig,
                            paramDef: paramDef
                        };
                    },
                    edit: function (containerEl, configDef, paramDef, ruleConfig, value) {

                        this.renderers["arca:set-association-rule-value"].currentCtx =
                        {
                            configDef: configDef,
                            ruleConfig: ruleConfig,
                            paramDef: paramDef
                        };

                        var me = this;
                        var selectEl = Selector.query('select', containerEl.parentNode)[0];
                        Event.addListener(selectEl,"change", function() {
                                me._renderPicker(configDef, containerEl, paramDef, ruleConfig);
                            }
                        );
                        this._renderPicker(configDef, containerEl, paramDef, ruleConfig);

                    }
                }
            },
            _renderPicker: function(configDef, containerEl, paramDef, ruleConfig)
            {
                var selectEl = Selector.query('select', containerEl.parentNode)[0];
                optionEl = selectEl.options[selectEl.selectedIndex];
                var targetType = optionEl.getAttribute("target");

                var domId = Alfresco.util.generateDomId(),
                    controlPickerDomId = domId + "-control-wrapper",
                    associationPicker = new Alfresco.module.ControlWrapper(domId).setOptions(
                        {
                            name: "associationPicker",
                            type: "association",
                            container: containerEl,
                            label: "Association Value",
                            value: (ruleConfig.parameterValues) ? ruleConfig.parameterValues[paramDef.namedValue] : null,
                            controlParams:
                            {
                                displayMode: "items",
                                multipleSelectMode: true,
                                //startLocation: "/app:company_home/st:sites/cm:taxonomy/cm:documentLibrary",
                                rootNode: "{sitehomes}"
                            },
                            field: {
                                endpointType: (targetType ? targetType : "cm:cmobject"),
                                endpointMany: true
                            },
                            fnValueChanged:
                            {
                                fn: function DRCA_SetSARAssociationValue_fnValueChanged(obj)
                                {
                                    var ctx = this.renderers["arca:set-association-rule-value"].currentCtx;
                                    this._setHiddenParameter(associationPicker.currentCtx.configDef, associationPicker.currentCtx.ruleConfig,
                                        "association-value", obj.selectedItems);
                                    this._updateSubmitElements(associationPicker.currentCtx.configDef);
                                },
                                scope: this
                            }
                        });
                associationPicker.eventGroup = associationPicker;
                associationPicker.currentCtx =
                {
                    configDef: configDef,
                    ruleConfig: ruleConfig,
                    paramDef: paramDef
                };

                associationPicker.render({
                    fn: function SARC_SetSARAssociationValue_fnOnTemplateLoaded()
                    {
                        // remove inlineable class, as it ruins our layout
                        var associationPickerDom = Dom.get(controlPickerDomId);
                        Dom.removeClass(associationPickerDom, "inlineable");
                    },
                    scope: this

                });
            },
            _createSelect: function (containerEl, configDef, paramDef, constraintOptions, value)
            {
                if (paramDef._type == "hidden")
                {
                    return this._createInputOfType(containerEl, configDef, paramDef, [], value, "hidden");
                }
                else
                {
                    var selectEl = document.createElement("select");
                    YAHOO.util.Dom.addClass(selectEl, "suppress-validation");
                    selectEl.setAttribute("name", "-");
                    selectEl.setAttribute("title", paramDef.displayLabel ? paramDef.displayLabel : paramDef.name);
                    selectEl.setAttribute("param", paramDef.name);
                    if (paramDef.isMultiValued)
                    {
                        selectEl.setAttribute("multiple", "true");
                        selectEl.setAttribute("size", "3");
                    }
                    if (containerEl)
                    {
                        containerEl.appendChild(selectEl);
                    }
                    if (!paramDef.isMandatory || !constraintOptions || constraintOptions.length == 0)
                    {
                        /**
                         * Create an empty options to select none-value OR
                         * since if there are no options in the select an error will be thrown
                         */
                        selectEl.appendChild(document.createElement("option"));
                    }
                    if (paramDef.isMandatory)
                    {
                        this._addValidation(selectEl, Alfresco.forms.validation.mandatory, configDef, "change");
                    }
                    if (constraintOptions)
                    {
                        var constraintOption,
                            optionEl;
                        for (var i = 0, l = constraintOptions.length; i < l; i++)
                        {
                            constraintOption = constraintOptions[i];
                            optionEl = document.createElement("option");
                            var constraintValue = "" + constraintOption.value;
                            optionEl.setAttribute("value", (constraintValue.indexOf("|") >= 0 ? constraintValue.substring(0,constraintValue.indexOf("|")) : constraintValue));
                            optionEl.setAttribute("target", (constraintValue.indexOf("|") >= 0  ? constraintValue.substring(constraintValue.indexOf("|")+1) : ""));
                            optionEl.appendChild(document.createTextNode(constraintOption.displayLabel ? constraintOption.displayLabel : constraintOption.value));
                            if ((constraintValue.indexOf("|") >= 0  ? constraintValue.substring(0,constraintValue.indexOf("|")) : constraintValue) == value)
                            {
                                optionEl.setAttribute("selected", "true");
                            }
                            selectEl.appendChild(optionEl);
                        }
                    }
                    return selectEl;
                }
            },

            /**
             * Populate a folder path from a nodeRef.
             *
             * @method _createPathSpan
             * @param containerEl {HTMLElement} Element within which the new span tag will be created
             * @param id {string} Dom ID to be given to span tag
             * @param nodeRef {string} NodeRef of folder
             */
            _createPathSpan: function (containerEl, configDef, paramDef, id, nodeRef)
            {
                var pathEl = document.createElement("span");
                Alfresco.util.generateDomId(pathEl, "association-value-path");
                containerEl.appendChild(pathEl);
                new Alfresco.Location(pathEl).setOptions(
                    {
                        siteId: this.options.siteId,
                        rootNode: this.options.rootNode
                    }).displayByNodeRef(nodeRef);
                return pathEl;
            },
        });

})();
