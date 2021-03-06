/*
 * Copyright 2018 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.features.hhs

import android.content.Context
import android.support.annotation.StringRes
import android.text.Html
import com.binaryfork.spanny.Spanny
import im.vector.R
import org.matrix.androidsdk.core.model.MatrixError

class ResourceLimitErrorFormatter(private val context: Context) {

    // 'hard' if the logged in user has been locked out, 'soft' if they haven't
    sealed class Mode(@StringRes val mauErrorRes: Int, @StringRes val defaultErrorRes: Int, @StringRes val contactRes: Int) {
        // User can still send message (will be used in a near future)
        object Soft : Mode(R.string.resource_limit_soft_mau, R.string.resource_limit_soft_default, R.string.resource_limit_soft_contact)

        // User cannot send message anymore
        object Hard : Mode(R.string.resource_limit_hard_mau, R.string.resource_limit_hard_default, R.string.resource_limit_hard_contact)
    }

    fun format(matrixError: MatrixError,
               mode: Mode,
               separator: CharSequence = " ",
               clickable: Boolean = false): CharSequence {
        val error = if (MatrixError.LIMIT_TYPE_MAU == matrixError.limitType) {
            context.getString(mode.mauErrorRes)
        } else {
            context.getString(mode.defaultErrorRes)
        }
        val contact = if (clickable && matrixError.adminUri != null) {
            val contactSubString = uriAsLink(matrixError.adminUri!!)
            val contactFullString = context.getString(mode.contactRes, contactSubString)
            Html.fromHtml(contactFullString)
        } else {
            val contactSubString = context.getString(R.string.resource_limit_contact_admin)
            context.getString(mode.contactRes, contactSubString)
        }
        return Spanny(error)
                .append(separator)
                .append(contact)
    }

    /**
     * Create a HTML link with a uri
     */
    private fun uriAsLink(uri: String): String {
        val contactStr = context.getString(R.string.resource_limit_contact_admin)
        return "<a href=\"$uri\">$contactStr</a>"
    }
}