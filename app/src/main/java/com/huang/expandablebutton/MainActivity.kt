package com.huang.expandablebutton

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.huang.library.ExpandableButton

class MainActivity : AppCompatActivity() {
    private var in_out_duration: Long = 800
    private var i = 0
    private var lastOffset: Float = 0f;


    internal var toolbarLayout: CollapsingToolbarLayout? = null

    internal var appBar: AppBarLayout? = null

    internal var expandableBtn: ExpandableButton? = null
    internal var expandableBtn2: ExpandableButton? = null

    internal var fl: FrameLayout? = null

    internal var mTextView: TextView? = null

    internal var hasHideButton: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews(){
        toolbarLayout = findViewById(R.id.toolbar_layout)
        appBar = findViewById(R.id.app_bar)
        expandableBtn = findViewById(R.id.expandableBtn)
        expandableBtn2 = findViewById(R.id.expandableBtn2)
        fl = findViewById(R.id.fl)
        mTextView = findViewById(R.id.tv_text)


        expandableBtn?.setFoldListener(ExpandableButton.FoldListener { isFolded, sfb ->
            if (isFolded) {
               Toast.makeText(this@MainActivity, "折叠 ", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@MainActivity, "展开 ", Toast.LENGTH_SHORT).show()
            }
        })
        expandableBtn?.setOnClickListener(ExpandableButton.OnClickListener {

            expandableBtn?.switchFoldStatus()
        })

        expandableBtn2?.setOnClickListener(object:ExpandableButton.OnClickListener{
            override fun onClick(sfb: ExpandableButton?) {
                Toast.makeText(this@MainActivity, "点击了", Toast.LENGTH_SHORT).show()
                sfb?.switchFoldStatus()
            }
        })

        appBar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            //防止初始化进来两次
            i++
            if (i <= 2) {
                return@OnOffsetChangedListener
            }

            if ( verticalOffset >= -100f && hasHideButton) {
                lastOffset = verticalOffset.toFloat()
                showButtonIn()
            } else if (verticalOffset < -100f && !hasHideButton) {
                lastOffset = verticalOffset.toFloat()
                showButtonOut()
            }
            //滑倒顶部状态
            if (verticalOffset == 0) {
                showButtonIn()
            }
        })
    }

    fun showButtonOut() {
        expandableBtn?.clearAnimation()
        var animator: ObjectAnimator = ObjectAnimator.
                ofFloat(expandableBtn, "translationX", -300f)
                .setDuration(in_out_duration);
        animator.addListener(
                object: Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        hasHideButton = true
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {
                        expandableBtn?.switchToFold()
                    }
                }
        )
        animator.start()
    }

    fun showButtonIn() {
        expandableBtn?.clearAnimation()
        var animator: ObjectAnimator = ObjectAnimator.
                ofFloat(expandableBtn, "translationX", 0f).
                setDuration(in_out_duration);
        animator.addListener(
                object: Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        expandableBtn?.switchToUnFold()
                        hasHideButton = false
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }
                }
        )
        animator.start();

    }
}
