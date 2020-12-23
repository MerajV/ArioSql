package com.github.databasehelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class CreateDatabase(private val context: Context){
    private lateinit var TABLE_NAME :String
    private lateinit var  DATABASE_NAME:String
    private  var DATABASE_VERSION:Int = 1
    private var COLUMNS:String = ""
    private var OnCreateTabels :MutableList<String> = ArrayList()
    private var CustomOnUpgradeTabels :MutableList<String> = ArrayList()
        private var OnUpgrade :MutableList<String> = ArrayList()
    private lateinit var db: SQLiteDatabase
    fun database(Name: String): CreateDatabase {
        DATABASE_NAME = Name
        return this
    }
    fun version(Version: Int): CreateDatabase {
        DATABASE_VERSION = Version
        return this
    }
    fun table(Name: String): CreateDatabase {
        TABLE_NAME = Name
        return this
    }
    fun column(Name: String, DInfo: String): CreateDatabase {
        COLUMNS += "$Name $DInfo,"
        return this
    }
fun onUpgradeCustomQuery (ConUpgrate:String){
    CustomOnUpgradeTabels.add(ConUpgrate)
}
    fun save(): CreateDatabase {
        COLUMNS = COLUMNS.substring(0, COLUMNS.length - 1);
        OnCreateTabels.add("CREATE TABLE $TABLE_NAME ($COLUMNS);")
        OnUpgrade.add("DROP TABLE IF EXISTS $TABLE_NAME;")
        COLUMNS = ""
        return this
        }
    fun init(){
        db = context.openOrCreateDatabase(
            DATABASE_NAME,
            Context.MODE_PRIVATE,
            null
        )
        val asd = db.version
        if(db.version == DATABASE_VERSION || db.version == 0) {
            if(!isTableExists(TABLE_NAME)){
                createTables(db)
                db.version = DATABASE_VERSION
            }
        }else{
            if(CustomOnUpgradeTabels.size > 0){
                upgradeTables(db,OnUpgrade)
                db.version = DATABASE_VERSION
            }else{
                upgradeTables(db,OnUpgrade)
                db.version = DATABASE_VERSION
            }
        }

           /*    if(db.version == DATABASE_VERSION){
            if(!isTableExists(TABLE_NAME)){
                db.execSQL(OnCreateTabels)
            }
        }else{
            // Upgrade
            if(CustomOnUpgradeTabels != ""){
                db.execSQL(CustomOnUpgradeTabels)
            }else{
                db.execSQL(OnUpgrade)
            }
            // Create Again
            db.execSQL(OnCreateTabels)
            db.version = DATABASE_VERSION*/


    }

    private fun createTables(db:SQLiteDatabase){
        OnCreateTabels.forEach {
            db.execSQL(it)
        }
    }
    private fun upgradeTables(db:SQLiteDatabase,UpgradeQueries :MutableList<String>){
        UpgradeQueries.forEach {
            db.execSQL(it)
        }
        createTables(db)
    }
    fun isTableExists(tableName: String): Boolean {
        val query =
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '$tableName'"
        db.rawQuery(query, null).use { cursor ->
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return true
                }
            }
            return false
        }
    }
}