package com.huang.expandablebutton

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import com.huang.library.ExpandableButton

class MainActivity : AppCompatActivity() {

    private var llOffDistance: Float = 0.toFloat()
    private var params: FrameLayout.LayoutParams? = null
    private var isUp = false   //判断是否为上滑状态
    private var isDown = false //判断是否为下拉状态
    private var i = 0
    private var lastOffset: Float = 0f;


    internal var toolbarLayout: CollapsingToolbarLayout? = null

    internal var appBar: AppBarLayout? = null

    internal var expandableBtn: ExpandableButton? = null

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
        expandableBtn = findViewById(R.id.text)
        fl = findViewById(R.id.fl)
        mTextView = findViewById(R.id.tv_text)

        expandableBtn?.setFoldListener(ExpandableButton.FoldListener { unFold, sfb ->
            if (unFold) {
                isUp = true
            }
            else {
                isDown = true
            }
        })
        expandableBtn?.setOnClickListener(ExpandableButton.OnClickListener {
            Log.i("TAG", "-----")
            expandableBtn?.switchFoldStatus()
        })

        appBar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            //防止初始化进来两次
            i++
            if (i <= 2) {
                return@OnOffsetChangedListener
            }

            if (params == null) {
                params = expandableBtn?.getLayoutParams() as FrameLayout.LayoutParams
                llOffDistance = params?.topMargin?.toFloat()?:0f
                isUp = true
                isDown = true
            }

            var distance: Float?  = llOffDistance + verticalOffset

            var gap: Float = distance?:0f - lastOffset

            //滑倒顶端状态 保持20的间距
            if (gap < 0 && gap >= -100f && hasHideButton) {
                lastOffset = distance?: 0f;
                showButtonIn()
            } else if (gap > 0 && gap >= 100f && !hasHideButton) {
                lastOffset = distance?: 0f;
                showButtonOut()
            }
            //滑倒底端状态
            if (verticalOffset == 0) {
                if (isDown && !expandableBtn?.isFolded()!!) {
                    //expandableBtn?.switchFoldStatus()
                }
            }
            params?.topMargin = distance?.toInt()
            fl?.requestLayout()
        })

    }

    fun showButtonOut() {
        if (isDown) {
            isDown = false
            var animator: ObjectAnimator = ObjectAnimator.ofFloat(expandableBtn, "translationX", -300f).setDuration(1000);
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
            animator.start();
        }
    }
    fun showButtonIn() {
        if (isUp) {
            isUp = false
            var animator: ObjectAnimator = ObjectAnimator.ofFloat(expandableBtn, "translationX", 0f).setDuration(1000);
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
}
