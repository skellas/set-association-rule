(function(){var b=YAHOO.util.Dom,r=YAHOO.util.Event,m=YAHOO.util.Element;var n=Alfresco.util.encodeHTML;Alfresco.SiteMembers=function(s){this.name="Alfresco.SiteMembers";this.id=s;this.widgets={};this.listWidgets={};this.buttons=[];this.modules={};this.isCurrentUserSiteAdmin=false;Alfresco.util.ComponentManager.register(this);Alfresco.util.YUILoaderHelper.require(["button","container","datasource","datatable","json"],this.onComponentsLoaded,this);YAHOO.Bubbling.on("deactivateAllControls",this.onDeactivateAllControls,this);return this};YAHOO.extend(Alfresco.SiteMembers,Alfresco.component.Base,{options:{siteId:"",minSearchTermLength:0,maxSearchResults:100,currentUser:"",currentUserRole:"",roles:[],error:null,setFocus:false},widgets:null,listWidgets:null,buttons:null,modules:null,isCurrentUserSiteAdmin:null,isSearching:false,onReady:function l(){var w=this;var v=Alfresco.constants.PROXY_URI+"api/sites/"+w.options.siteId+"/memberships?";this.widgets.dataSource=new YAHOO.util.DataSource(v,{responseType:YAHOO.util.DataSource.TYPE_JSON,connXhrMode:"queueRequests",responseSchema:{resultsList:"items"}});this.widgets.dataSource.doBeforeParseData=function t(C,B){var A=B;if(B){var z=[],E,F;for(var y=0,D=B.length;y<D;y++){E=B[y];F=E.authority;F.role=E.role;F.isMemberOfGroup=E.isMemberOfGroup;z.push(F)}z.sort(function(G,x){var I=G.firstName+G.lastName,H=x.firstName+x.lastName;return(I>H)?1:(I<H)?-1:0});A={items:z}}return A};if(w.options.currentUserRole!==undefined&&w.options.currentUserRole==="SiteManager"){this.isCurrentUserSiteAdmin=true}this._setupDataTable();this.widgets.searchButton=Alfresco.util.createYUIButton(this,"button",this.onSearch);if(b.get(this.id+"-invitePeople")){this.widgets.invitePeople=Alfresco.util.createYUIButton(this,"invitePeople",null,{type:"link"})}var s=b.get(this.id+"-term"),u=new YAHOO.util.KeyListener(s,{keys:13},{fn:function(){w.onSearch()},scope:this,correctScope:true},"keydown");u.enable();if(this.options.error){u.disable();this.widgets.dataTable.set("MSG_ERROR",this.options.error);this.widgets.dataTable.showTableMessage(this.options.error,YAHOO.widget.DataTable.CLASS_ERROR);YAHOO.Bubbling.fire("deactivateAllControls")}if(this.options.setFocus){s.focus()}if(window.location.hash==="#showall"||this.options.minSearchTermLength===0){this.onSearch()}b.setStyle(this.id+"-body","visibility","visible")},_setupDataTable:function p(){var t=this;var u=[{key:"userName",label:"User Name",sortable:false,formatter:this.bind(this.renderCellAvatar),width:64},{key:"bio",label:"Bio",sortable:false,formatter:this.bind(this.renderCellDescription)},{key:"role",label:"Select Role",formatter:this.bind(this.renderCellRoleSelect),width:140},{key:"uninvite",label:"Uninvite",formatter:this.bind(this.renderCellUninvite),width:80}];this.widgets.dataTable=new YAHOO.widget.DataTable(this.id+"-members",u,this.widgets.dataSource,{renderLoopSize:32,initialLoad:false,MSG_EMPTY:'<span style="white-space: nowrap;">'+this.msg("site-members.enter-search-term")+"</span>"});this.widgets.dataTable.doBeforeLoadData=function s(w,x,z){if(x.error){try{var v=YAHOO.lang.JSON.parse(x.responseText);t.widgets.dataTable.set("MSG_ERROR",v.message)}catch(y){t._setDefaultDataTableErrors(t.widgets.dataTable)}}else{if(x.results){if(x.results.length===0){t.widgets.dataTable.set("MSG_EMPTY",'<span style="white-space: nowrap;">'+t.msg("message.empty")+"</span>")}t.renderLoopSize=Alfresco.util.RENDERLOOPSIZE}}return true}},renderCellAvatar:function e(w,t,x,y){b.setStyle(w.parentNode,"width",x.width+"px");var v=t.getData("userName"),u=Alfresco.constants.URL_PAGECONTEXT+"user/"+v+"/profile",s=Alfresco.constants.URL_RESCONTEXT+"components/images/no-user-photo-64.png";if(t.getData("avatar")!==undefined){s=Alfresco.constants.PROXY_URI+t.getData("avatar")+"?c=queue&ph=true"}w.innerHTML='<a href="'+u+'"><img src="'+s+'" alt="avatar" /></a>'},renderCellDescription:function j(C,F,z,w){var B=F.getData("userName"),v=B,D=F.getData("firstName"),E=F.getData("lastName"),s=F.getData("userStatus"),x=F.getData("userStatusTime");if((D!==undefined)||(E!==undefined)){v=D?D+" ":"";v+=E?E:""}var u=Alfresco.constants.URL_PAGECONTEXT+"user/"+B+"/profile",A=F.getData("jobtitle")?F.getData("jobtitle"):"",t=F.getData("organization")?F.getData("organization"):"",y='<h3><a href="'+u+'">'+n(v)+"</a></h3>";if(A.length>0){y+='<div><span class="attr-name">'+this.msg("label.title")+': </span>&nbsp;<span class="attr-value">'+n(A)+"</span></div>"}if(t.length>0){y+='<div><span class="attr-name">'+this.msg("label.company")+':</span>&nbsp;<span class="attr-value">'+n(t)+"</span></div>"}if(typeof s!="undefined"&&s.length>0){y+='<div class="user-status">'+n(s)+" <span>("+Alfresco.util.relativeTime(Alfresco.util.fromISO8601(x.iso8601))+")</span></div>"}C.innerHTML=y},getRoles:function(s){return this.options.roles},renderCellRoleSelect:function a(C,E,y,t){b.setStyle(C.parentNode,"width",y.width+"px");b.setStyle(C.parentNode,"text-align","right");b.addClass(C,"overflow");var u=E.getData("role");if(this.isCurrentUserSiteAdmin&&E.getData("userName")!==this.options.currentUser){var B=E.getData("userName");C.innerHTML='<span id="'+this.id+"-roleSelector-"+B+'"></span>';var z=[],w=this.getRoles(E.getData()),v;for(var A=0,s=w.length;A<s;A++){v=w[A];z.push({text:this.msg("role."+v),value:v,onclick:{fn:this.onRoleSelect,obj:{user:B,currentRole:u,newRole:v,recordId:E.getId()},scope:this}})}var D=new YAHOO.widget.Button({container:this.id+"-roleSelector-"+B,type:"menu",label:this.msg("role."+u)+" "+Alfresco.constants.MENU_ARROW_SYMBOL,menu:z});this.listWidgets[B]={roleSelector:D};this.buttons[B+"-roleSelector"]={roleSelector:D}}else{C.innerHTML="<div>"+this.msg("role."+u)+"</div>"}},renderCellUninvite:function o(v,t,w,x){b.setStyle(v.parentNode,"width",w.width+"px");if(this.isCurrentUserSiteAdmin){var u=t.getData("userName");v.innerHTML='<span id="'+this.id+"-button-"+u+'"></span>';var s=new YAHOO.widget.Button({container:this.id+"-button-"+u,label:this.msg("site-members.uninvite"),disabled:t.getData("isMemberOfGroup"),onclick:{fn:this.doRemove,obj:u,scope:this}});this.buttons[u+"-button"]={button:s}}else{v.innerHTML="<div></div>"}},onSearch:function g(){var s=YAHOO.lang.trim(b.get(this.id+"-term").value);if(s.replace(/\*/g,"").length<this.options.minSearchTermLength){Alfresco.util.PopupManager.displayMessage({text:this.msg("message.minimum-length",this.options.minSearchTermLength)});return}this._performSearch(s)},doRemove:function h(v,s){this.widgets.feedbackMessage=Alfresco.util.PopupManager.displayMessage({text:this.msg("message.removing"),spanClass:"wait",displayTime:0});var w=function x(A,z){this.widgets.feedbackMessage.hide();Alfresco.util.PopupManager.displayMessage({text:this.msg("site-members.remove-success",z)});var y=this.widgets.dataTable.getRecordIndex(v.target.id);this.widgets.dataTable.deleteRow(y)};var t=function u(y){this.widgets.feedbackMessage.hide()};Alfresco.util.Ajax.request({url:Alfresco.constants.PROXY_URI+"api/sites/"+this.options.siteId+"/memberships/"+encodeURIComponent(s),method:"DELETE",successCallback:{fn:w,obj:s,scope:this},failureMessage:this.msg("site-members.remove-failure",s,this.options.siteId),failureCallback:{fn:t,scope:this}})},onRoleSelect:function f(C,s,B){var z=this.widgets.dataTable.getRecord(B.recordId);var y=z.getData();var x=this.widgets.dataTable.getRecordIndex(z);var u=y.role;var D=B.newRole;var w=B.user;if(D!==u){this.widgets.feedbackMessage=Alfresco.util.PopupManager.displayMessage({text:this.msg("message.changingrole"),spanClass:"wait",effect:null,displayTime:0});var E=function v(F,G){this.widgets.feedbackMessage.hide();Alfresco.util.PopupManager.displayMessage({text:this.msg("site-members.change-role-success",G.user,this.msg("role."+G.role)),effect:null});var H=this.widgets.dataTable.getRecord(G.recordIndex).getData();H.role=B.newRole;this.widgets.dataTable.updateRow(G.recordIndex,H)};var t=function A(F){this.widgets.feedbackMessage.hide()};Alfresco.util.Ajax.jsonPut({url:Alfresco.constants.PROXY_URI+"api/sites/"+this.options.siteId+"/memberships",dataObj:{role:D,person:{userName:w}},successCallback:{fn:E,obj:{user:w,role:D,recordIndex:x},scope:this},failureMessage:this.msg("site-members.change-role-failure",w),failureCallback:{fn:t,scope:this}})}},onDeactivateAllControls:function i(u,t){var s,v,w=Alfresco.util.disableYUIButton;for(s in this.widgets){if(this.widgets.hasOwnProperty(s)){w(this.widgets[s])}}},_setDefaultDataTableErrors:function k(s){var t=Alfresco.util.message;s.set("MSG_EMPTY",t("message.empty","Alfresco.SiteMembers"));s.set("MSG_ERROR",t("message.error","Alfresco.SiteMembers"))},_performSearch:function c(s){if(!this.isSearching){this.isSearching=true;this._setDefaultDataTableErrors(this.widgets.dataTable);this.widgets.dataTable.set("MSG_EMPTY",this.msg("site-members.searching"));this.widgets.dataTable.deleteRows(0,this.widgets.dataTable.getRecordSet().getLength());function t(v,w,x){this._enableSearchUI();this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable,v,w,x);this._setDefaultDataTableErrors(this.widgets.dataTable)}function u(w,x){this._enableSearchUI();if(x.status==401){window.location.reload()}else{try{var v=YAHOO.lang.JSON.parse(x.responseText);this.widgets.dataTable.set("MSG_ERROR",v.message);this.widgets.dataTable.showTableMessage(v.message,YAHOO.widget.DataTable.CLASS_ERROR)}catch(y){this._setDefaultDataTableErrors(this.widgets.dataTable)}}}this.widgets.dataSource.sendRequest(this._buildSearchParams(s),{success:t,failure:u,scope:this});this.widgets.searchButton.set("disabled",true);YAHOO.lang.later(2000,this,function(){if(this.isSearching){if(!this.widgets.feedbackMessage){this.widgets.feedbackMessage=Alfresco.util.PopupManager.displayMessage({text:Alfresco.util.message("message.searching",this.name),spanClass:"wait",displayTime:0})}else{if(!this.widgets.feedbackMessage.cfg.getProperty("visible")){this.widgets.feedbackMessage.show()}}}},[])}},_enableSearchUI:function q(){if(this.widgets.feedbackMessage&&this.widgets.feedbackMessage.cfg.getProperty("visible")){this.widgets.feedbackMessage.hide()}this.widgets.searchButton.set("disabled",false);this.isSearching=false},_buildSearchParams:function d(s){var t=YAHOO.lang.substitute("size={maxResults}&nf={term}&authorityType=USER",{maxResults:this.options.maxSearchResults,term:encodeURIComponent(s)});return t}})})();