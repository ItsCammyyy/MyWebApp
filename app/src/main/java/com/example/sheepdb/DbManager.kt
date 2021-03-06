package com.example.sheepdb

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbManager {

    //database name
    var dbName = "MySheep"
    //table name
    var dbTable = "Sheep"
    //columns
    var colID = "ID"
    var colTitle = "Title"
    var colDes = "Details"
    //database version
    var dbVersion = 1

    //Create table if not exists MySheep (ID Integer Primary Key, title Text, description Text) ;"
    var sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $dbTable ($colID INTEGER PRIMARY KEY,$colTitle TEXT, $colDes TEXT);"



    var sqlDB:SQLiteDatabase?=null

    constructor(context: Context) {
        var db = DatabaseHelperSheep(context)
        sqlDB = db.writableDatabase
    }



    inner class DatabaseHelperSheep:SQLiteOpenHelper{
        var context: Context?=null
        constructor(context: Context):super(context,dbName,null, dbVersion){
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context, "database created...", Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table if Exists $dbTable")
        }




    }

    fun insert(values: ContentValues):Long{
        val ID = sqlDB!!.insert(dbTable, "",values)
        return ID
    }

    fun Query(projection:Array<String>, selection:String, selectionArgs:Array<String>, sorOrder:String):Cursor{
        val qb = SQLiteQueryBuilder();
        qb.tables = dbTable
        val cursor = qb.query(sqlDB, projection, selection, selectionArgs, null, null, sorOrder)
        return cursor
    }

    fun delete(selection:String, selectionArgs:Array<String>): Int{
        val count = sqlDB!!.delete(dbTable, selection, selectionArgs)
        return count
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.update(dbTable, values, selection, selectionArgs)
        return count
    }
}