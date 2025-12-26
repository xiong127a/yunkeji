package org.yun.dao.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class UserBalanceRecordTableDef extends TableDef {
    
    public static final UserBalanceRecordTableDef USER_BALANCE_RECORD = new UserBalanceRecordTableDef();
    
    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");
    public final QueryColumn CHANGE_AMOUNT = new QueryColumn(this, "change_amount");
    public final QueryColumn CHANGE_TYPE = new QueryColumn(this, "change_type");
    public final QueryColumn RELATED_RECORD_ID = new QueryColumn(this, "related_record_id");
    public final QueryColumn REMARK = new QueryColumn(this, "remark");
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");
    
    public UserBalanceRecordTableDef() {
        super("yunkeji", "user_balance_record");
    }
}






























