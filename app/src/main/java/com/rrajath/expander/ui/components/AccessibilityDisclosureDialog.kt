package com.rrajath.expander.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rrajath.expander.R

/**
 * The prominent in-app disclosure for the accessibility service.
 *
 * Two modes:
 *  - Consent gate ([onAgree] non-null): shown before the user is sent to the
 *    system Accessibility Settings screen. "Agree and continue" records consent
 *    (see [com.rrajath.expander.util.AccessibilityDisclosure]) and runs
 *    [onAgree]; "Not now" just dismisses.
 *  - Review ([onAgree] null): the same text, reachable later from Settings, with
 *    a single "Close" button.
 */
@Composable
fun AccessibilityDisclosureDialog(
    onDismiss: () -> Unit,
    onAgree: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.accessibility_disclosure_title)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DisclosureSection(
                    R.string.accessibility_disclosure_access_heading,
                    R.string.accessibility_disclosure_access_body
                )
                DisclosureSection(
                    R.string.accessibility_disclosure_why_heading,
                    R.string.accessibility_disclosure_why_body
                )
                DisclosureSection(
                    R.string.accessibility_disclosure_data_heading,
                    R.string.accessibility_disclosure_data_body
                )
                DisclosureSection(
                    R.string.accessibility_disclosure_not_heading,
                    R.string.accessibility_disclosure_not_body
                )
                Text(
                    text = stringResource(R.string.accessibility_disclosure_revoke),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            if (onAgree != null) {
                TextButton(onClick = onAgree) {
                    Text(stringResource(R.string.accessibility_disclosure_agree))
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.accessibility_disclosure_close))
                }
            }
        },
        dismissButton = if (onAgree != null) {
            {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.accessibility_disclosure_decline))
                }
            }
        } else null
    )
}

@Composable
private fun DisclosureSection(headingRes: Int, bodyRes: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = stringResource(headingRes),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(bodyRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
