package jp.techacademy.kenta.kakumoto.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.os.Build
import android.os.Handler
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    var cursor: Cursor? = null
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
           getContentsInfo()
        }
        
        play_button.setOnClickListener{
            if(play_button.text == "再生"){
                play_button.text = "停止"
                forward_button.isClickable = false
                back_button.isClickable = false

                mTimer = Timer()
                mTimer!!.schedule(object: TimerTask(){
                    override fun run(){
                        mHandler.post{
                            if(cursor!!.moveToNext()){
                                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor!!.getLong(fieldIndex)
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                imageView.setImageURI(imageUri)
                                Log.d("TEST","cursorは"+cursor+"idは"+id)
                            }else{
                                cursor!!.moveToFirst()
                                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor!!.getLong(fieldIndex)
                                Log.d("TEST","最初に戻る idは"+id)
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                imageView.setImageURI(imageUri)
                            }
                        }
                    }
                }, 2000, 2000)


            }else{
                play_button.text = "再生"
                forward_button.isClickable = true
                back_button.isClickable = true
                mTimer!!.cancel()

            }
        }

        forward_button.setOnClickListener{
            if(cursor!!.moveToNext()){
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
                Log.d("TEST","cursorは"+cursor+"idは"+id+"fieldindexは"+fieldIndex)
            }else{
                cursor!!.moveToFirst()
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                Log.d("TEST","最初に戻る idは"+id)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }
        }

        back_button.setOnClickListener{
            if(cursor!!.moveToPrevious()){
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
                Log.d("TEST","戻る：cursorは"+cursor+"idは"+id)
            }else{
                cursor!!.moveToLast()
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    forward_button.isClickable = true
                    back_button.isClickable = true
                    play_button.isClickable = true
                    getContentsInfo()
                }else{
                    Log.d("TEST","許可されなかった")
                    forward_button.isClickable = false
                    back_button.isClickable = false
                    play_button.isClickable = false
                    }
        }
    }

    private fun getContentsInfo(){
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if(cursor!!.moveToFirst()) {
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
            Log.d("TEST","初期画像表示 idは"+id)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("TEST","Start")
    }

    override fun onResume() {
        super.onResume()
        Log.d("TEST","Resume")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                forward_button.isClickable = true
                back_button.isClickable = true
                play_button.isClickable = true
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                forward_button.isClickable = false
                back_button.isClickable = false
                play_button.isClickable = false

            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
            forward_button.isClickable = true
            back_button.isClickable = true
            play_button.isClickable = true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("TEST","Pause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("TEST","Stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TEST","Destroy")
        cursor!!.close()
    }
}