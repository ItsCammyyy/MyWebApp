package com.example.sheepdb

import android.app.SearchManager
import android.text.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    var listSheep = ArrayList<Sheep>()
    var cursor:Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Load from DB
        LoadQuery("%")


    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    private fun LoadQuery(title: String) {
        var dbManager =  DbManager(this)
        val projections = arrayOf("ID", "Title", "Details")
        val selectionArgs = arrayOf(title)
        var cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listSheep.clear()
        if (cursor.moveToFirst()){
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Details"))

                listSheep.add(Sheep(ID, Title, Description))

            }while (cursor.moveToNext())
        }

        //adapter
        var mySheepAdapter = MySheepAdapter(this, listSheep)

////        //set adapter
        notesLv.adapter = mySheepAdapter
////        mySheepAdapter.notifyDataSetChanged()
//
//        //get total number of tasks from ListView
//        val total = notesLv.count
//        //actionbar
//        val mActionBar = supportActionBar
//        if (mActionBar != null) {
//            //set to actionbar as subtitle of actionbar
//            mActionBar.subtitle = "You have ${total}sheep(s) in list..."
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        //searchView
        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%"+query+"%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%"+newText+"%")
                return false
            }
        });

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when(item.itemId){
                R.id.addSheep->{
                    startActivity(Intent(this, AddSheepActivity::class.java))
                }
                R.id.action_settings->{
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MySheepAdapter : BaseAdapter {
        var listSheepAdapter = ArrayList<Sheep>()
        var context:Context?=null

        constructor(context: Context, listSheepAdapter: ArrayList<Sheep>) : super() {
            this.listSheepAdapter = listSheepAdapter
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //inflate layout row.xml
            var myView = layoutInflater.inflate(R.layout.row, null)
            var mySheep = listSheepAdapter[position]
            myView.titleTv.text = mySheep.nodeName
            myView.descTv.text = mySheep.nodeDes

            //delete button click
            myView.deleteBtn.setOnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(mySheep.nodeID.toString())
                dbManager.delete("ID=?", selectionArgs)
                LoadQuery("%")
            }
            //edit//update button click
            myView.editBtn.setOnClickListener {
                GoToUpdateFun(mySheep)
            }
            //copy btn click
            myView.copyBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatinate
                val s = title +"\n"+ desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s // add to  clipboard
                Toast.makeText(this@MainActivity, "Copied. . .", Toast.LENGTH_SHORT).show()
            }

            //share btn click
            myView.shareBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatinate
                val s = title +"\n"+ desc
                //share intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listSheepAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listSheepAdapter.size

        }

    }

    private fun GoToUpdateFun(mySheep: Sheep) {
        var intent = Intent(this, AddSheepActivity::class.java)
        intent.putExtra("ID", mySheep.nodeID) //put id
        intent.putExtra("name", mySheep.nodeName) //put name
        intent.putExtra("des", mySheep.nodeDes) //put description
        startActivity(intent) //start activity
    }


}
