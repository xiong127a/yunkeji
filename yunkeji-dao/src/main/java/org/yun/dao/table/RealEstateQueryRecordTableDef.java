package org.yun.dao.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class RealEstateQueryRecordTableDef extends TableDef {
    
    public static final RealEstateQueryRecordTableDef REAL_ESTATE_QUERY_RECORD = new RealEstateQueryRecordTableDef();
    
    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");
    public final QueryColumn NAME = new QueryColumn(this, "name");
    public final QueryColumn ID_CARD = new QueryColumn(this, "id_card");
    public final QueryColumn REQUEST_NO = new QueryColumn(this, "request_no");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn RESULT = new QueryColumn(this, "result");
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");
    
    public RealEstateQueryRecordTableDef() {
        super("yunkeji", "real_estate_query_record");
    }
}