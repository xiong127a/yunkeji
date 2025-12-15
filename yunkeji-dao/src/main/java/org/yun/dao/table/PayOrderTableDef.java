package org.yun.dao.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class PayOrderTableDef extends TableDef {
    
    public static final PayOrderTableDef PAY_ORDER = new PayOrderTableDef();
    
    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn ORDER_NO = new QueryColumn(this, "order_no");
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");
    public final QueryColumn QUERY_RECORD_ID = new QueryColumn(this, "query_record_id");
    public final QueryColumn AMOUNT = new QueryColumn(this, "amount");
    public final QueryColumn PAY_CHANNEL = new QueryColumn(this, "pay_channel");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn QR_CONTENT = new QueryColumn(this, "qr_content");
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");
    
    public PayOrderTableDef() {
        super("yunkeji", "pay_order");
    }
}

















