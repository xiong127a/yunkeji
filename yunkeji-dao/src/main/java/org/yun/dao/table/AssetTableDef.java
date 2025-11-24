package org.yun.dao.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class AssetTableDef extends TableDef {
    
    public static final AssetTableDef ASSET = new AssetTableDef();
    
    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");
    public final QueryColumn NAME = new QueryColumn(this, "name");
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");
    public final QueryColumn FILE_PATH = new QueryColumn(this, "file_path");
    public final QueryColumn FILE_NAME = new QueryColumn(this, "file_name");
    public final QueryColumn FILE_SIZE = new QueryColumn(this, "file_size");
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");
    
    public AssetTableDef() {
        super("yunkeji", "asset");
    }
}