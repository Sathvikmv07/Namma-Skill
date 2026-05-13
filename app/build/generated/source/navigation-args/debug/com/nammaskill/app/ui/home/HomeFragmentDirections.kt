package com.nammaskill.app.ui.home

import android.os.Bundle
import androidx.navigation.NavDirections
import com.nammaskill.app.R
import kotlin.Int
import kotlin.String

public class HomeFragmentDirections private constructor() {
  private data class ActionHomeToDetail(
    public val courseId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_home_to_detail

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("courseId", this.courseId)
        return result
      }
  }

  private data class ActionHomeToApply(
    public val courseId: String,
    public val courseName: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_home_to_apply

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("courseId", this.courseId)
        result.putString("courseName", this.courseName)
        return result
      }
  }

  public companion object {
    public fun actionHomeToDetail(courseId: String): NavDirections = ActionHomeToDetail(courseId)

    public fun actionHomeToApply(courseId: String, courseName: String): NavDirections =
        ActionHomeToApply(courseId, courseName)
  }
}
