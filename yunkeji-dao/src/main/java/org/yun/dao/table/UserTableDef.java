package org.yun.dao.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class UserTableDef extends TableDef {
    
    public static final UserTableDef USER = new UserTableDef();
    
    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn USERNAME = new QueryColumn(this, "username");
    public final QueryColumn EMAIL = new QueryColumn(this, "email");
    public final QueryColumn PASSWORD = new QueryColumn(this, "password");
    public final QueryColumn ROLE = new QueryColumn(this, "role");
    public final QueryColumn QUERY_PRICE = new QueryColumn(this, "query_price");
    public final QueryColumn BALANCE = new QueryColumn(this, "balance");
    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");
    public final QueryColumn DEPTH = new QueryColumn(this, "depth");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn KYC_STATUS = new QueryColumn(this, "kyc_status");
    public final QueryColumn ID_CARD_FRONT_URL = new QueryColumn(this, "id_card_front_url");
    public final QueryColumn ID_CARD_BACK_URL = new QueryColumn(this, "id_card_back_url");
    public final QueryColumn BUSINESS_LICENSE_URL = new QueryColumn(this, "business_license_url");
    public final QueryColumn TRUSTED = new QueryColumn(this, "trusted");
    
    public UserTableDef() {
        super("yunkeji", "user");
    }
}