package pl.krzysztofwojciechowski.langcourses.ui.main


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.krzysztofwojciechowski.langcourses.R

@LargeTest
@RunWith(AndroidJUnit4::class)
class TestQuizSetsProgress {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(CourseListActivity::class.java)

    @Test
    fun testQuizSetsProgress() {
        Thread.sleep(2000)
        val appCompatButton = onView(
            allOf(
                withId(R.id.rv_main_courses_startcont_btn), withText("Start"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.rv_main_courses_card),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        Thread.sleep(40000)

        /*
        val test = onView(
            allOf(
                withId(R.id.rv_course_chapter_box),
                isDisplayed()
            )
        )
        var keepWaiting = false
        val endTime = System.currentTimeMillis() + 60000
        do {
            test.withFailureHandler { _, _ -> keepWaiting = true }.check(matches(isDisplayed()))
        } while (keepWaiting && System.currentTimeMillis() < endTime)
        */

        val constraintLayout = onView(
            allOf(
                withId(R.id.rv_course_chapter_box),
                childAtPosition(
                    allOf(
                        withId(R.id.cc_rv_course_chapters),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        constraintLayout.perform(click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.startLearningButton), withText("Start learning"),
                childAtPosition(
                    allOf(
                        withId(R.id.top_cl),
                        childAtPosition(
                            withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                            3
                        )
                    ),
                    10
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        Thread.sleep(5000)

        val tabView = onView(
            allOf(
                withContentDescription("Quiz"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tabs),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.quiz_button), withText("Start"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_img_layout),
                        withParent(withId(R.id.view_pager))
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())

        val appCompatRadioButton = onView(
            allOf(
                withId(R.id.quiz_answer1), withText("cat"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_answer_rg),
                        childAtPosition(
                            withId(R.id.quiz_text),
                            1
                        )
                    ),
                    0
                )
            )
        )
        appCompatRadioButton.perform(scrollTo(), click())

        val appCompatButton4 = onView(
            allOf(
                withId(R.id.quiz_button), withText("Check"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_img_layout),
                        withParent(withId(R.id.view_pager))
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatButton4.perform(click())

        val appCompatButton5 = onView(
            allOf(
                withId(R.id.quiz_button), withText("Next"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_img_layout),
                        withParent(withId(R.id.view_pager))
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatButton5.perform(click())

        val appCompatRadioButton2 = onView(
            allOf(
                withId(R.id.quiz_answer2), withText("Have you got a pet?"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_answer_rg),
                        childAtPosition(
                            withId(R.id.quiz_text),
                            1
                        )
                    ),
                    1
                )
            )
        )
        appCompatRadioButton2.perform(scrollTo(), click())

        val appCompatButton6 = onView(
            allOf(
                withId(R.id.quiz_button), withText("Check"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_img_layout),
                        withParent(withId(R.id.view_pager))
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatButton6.perform(click())

        val appCompatButton7 = onView(
            allOf(
                withId(R.id.quiz_button), withText("Next"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_img_layout),
                        withParent(withId(R.id.view_pager))
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatButton7.perform(click())

        val appCompatImageView = onView(
            allOf(
                withId(R.id.quiz_img_answer2),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_img),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            0
                        )
                    ),
                    1
                )
            )
        )
        appCompatImageView.perform(scrollTo(), click())

        val appCompatButton8 = onView(
            allOf(
                withId(R.id.quiz_button), withText("Check"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_img_layout),
                        withParent(withId(R.id.view_pager))
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatButton8.perform(click())

        val appCompatButton9 = onView(
            allOf(
                withId(R.id.quiz_button), withText("Next"),
                childAtPosition(
                    allOf(
                        withId(R.id.quiz_img_layout),
                        withParent(withId(R.id.view_pager))
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatButton9.perform(click())

        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.chapter_toolbar),
                        childAtPosition(
                            withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        Thread.sleep(5000)

        val textView = onView(
            allOf(
                withId(R.id.cc_percent),
                isDisplayed()
            )
        )
        textView.check(matches(withText("33%")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
