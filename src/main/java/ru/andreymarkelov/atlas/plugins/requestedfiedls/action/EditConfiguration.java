package ru.andreymarkelov.atlas.plugins.requestedfiedls.action;

import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;

import ru.andreymarkelov.atlas.plugins.requestedfiedls.manager.PluginData;
import ru.andreymarkelov.atlas.plugins.requestedfiedls.model.JSONFieldData;

public class EditConfiguration extends AbstractEditConfigurationItemAction {
    private static final long serialVersionUID = -4644319955468389371L;

    private final PluginData pluginData;
    private final JiraAuthenticationContext authenticationContext;
    private final GlobalPermissionManager globalPermissionManager;

    private String url;
    private String user;
    private String password;
    private String reqType;
    private String reqData;
    private String reqPath;

    public EditConfiguration(
            ManagedConfigurationItemService managedConfigurationItemService,
            PluginData pluginData,
            JiraAuthenticationContext authenticationContext,
            GlobalPermissionManager globalPermissionManager) {
        super(managedConfigurationItemService);
        this.pluginData = pluginData;
        this.authenticationContext = authenticationContext;
        this.globalPermissionManager = globalPermissionManager;
    }

    @Override
    public String doDefault() throws Exception {
        JSONFieldData data = pluginData.getJSONFieldData(getFieldConfig());
        if (data != null) {
            this.url = data.getUrl();
            this.user = data.getUser();
            this.password = data.getPassword();
            this.reqType = data.getReqType();
            this.reqData = data.getReqData();
            this.reqPath = data.getReqPath();
        }

        return INPUT;
    }

    @Override
    @RequiresXsrfCheck
    protected String doExecute() throws Exception {
        if (!globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, getLoggedInUser())) {
            return "securitybreach";
        }

        String reqDataType = isXmlField() ? "xml" : "json";
        pluginData.storeJSONFieldData(getFieldConfig(), new JSONFieldData(url, user, password, reqType, reqDataType, reqData, reqPath));
        return getRedirect("/secure/admin/ConfigureCustomField!default.jspa?customFieldId=" + getFieldConfig().getCustomField().getIdAsLong().toString());
    }

    @Override
    protected void doValidation() {
        if (url ==null || url.length() == 0) {
            addError("url", authenticationContext.getI18nHelper().getText("ru.andreymarkelov.atlas.plugins.requestedfields.fieldconfig.url.error.empty"));
        }

        if (reqPath ==null || reqPath.length() == 0) {
            addError(
                    "reqPath",
                    authenticationContext.getI18nHelper().getText(isXmlField() ?
                            "ru.andreymarkelov.atlas.plugins.requestedfields.fieldconfig.reqdata.xml.error.empty" :
                            "ru.andreymarkelov.atlas.plugins.requestedfields.fieldconfig.reqdata.json.error.empty"));
        }
    }

    public String getPassword() {
        return password;
    }

    public String getReqData() {
        return reqData;
    }

    public String getReqPath() {
        return reqPath;
    }

    public String getReqType() {
        return reqType;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public boolean isXmlField() {
        return (getCustomField().getCustomFieldType().getKey().equals("ru.andreymarkelov.atlas.plugins.requestedfields:xml-request-custom-field") ||
                getCustomField().getCustomFieldType().getKey().equals("ru.andreymarkelov.atlas.plugins.requestedfields:xml-multi-request-custom-field"));
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setReqData(String reqData) {
        this.reqData = reqData;
    }

    public void setReqPath(String reqPath) {
        this.reqPath = reqPath;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
