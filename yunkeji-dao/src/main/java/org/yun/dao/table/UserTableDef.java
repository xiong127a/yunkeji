package org.yun.dao.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class UserTableDef extends TableDef {
    
    public static final UserTableDef USER = new UserTableDef();
    
    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn USERNAME = new QueryColumn(this, "username");
    public final QueryColumn EMAIL = new QueryColumn(this, "email");
    public final QueryColumn PASSWORD = new QueryColumn(this, "password");
    
    public UserTableDef() {
        super("yunkeji", "user");
    }
}