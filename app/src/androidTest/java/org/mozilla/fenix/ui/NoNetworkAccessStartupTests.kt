/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.ui

import androidx.core.net.toUri
import androidx.test.espresso.IdlingRegistry
import org.junit.After
import org.junit.Test
import org.mozilla.fenix.R
import org.mozilla.fenix.helpers.HomeActivityTestRule
import org.mozilla.fenix.helpers.TestHelper.packageName
import org.mozilla.fenix.helpers.TestHelper.setNetworkConnection
import org.mozilla.fenix.helpers.TestHelper.verifyUrl
import org.mozilla.fenix.helpers.idlingresource.NetworkConnectedIdlingResource
import org.mozilla.fenix.helpers.idlingresource.NetworkDisconnectedIdlingResource
import org.mozilla.fenix.ui.robots.browserScreen
import org.mozilla.fenix.ui.robots.homeScreen
import org.mozilla.fenix.ui.robots.navigationToolbar

/**
 * Tests to verify some main UI flows with Network connection off
 *
 */

class NoNetworkAccessStartupTests {
    private val activityTestRule = HomeActivityTestRule()
    private val networkDisconnectedIdlingResource = NetworkDisconnectedIdlingResource()

    @After
    fun tearDown() {
        setNetworkConnection(true)
        IdlingRegistry.getInstance().unregister(NetworkConnectedIdlingResource())
    }

    @Test
    // Based on STR from https://github.com/mozilla-mobile/fenix/issues/16886
    fun noNetworkConnectionStartupTest() {
        setNetworkConnection(false)

        activityTestRule.launchActivity(null)

        IdlingRegistry.getInstance().unregister(networkDisconnectedIdlingResource)

        homeScreen {
        }.dismissOnboarding()
        homeScreen {
            verifyHomeScreen()
        }
    }

    @Test
    // Based on STR from https://github.com/mozilla-mobile/fenix/issues/16886
    fun networkInterruptedFromBrowserToHomeTest() {
        val url = "example.com"

        activityTestRule.launchActivity(null)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(url.toUri()) {}

        setNetworkConnection(false)
        IdlingRegistry.getInstance().unregister(networkDisconnectedIdlingResource)

        browserScreen {
        }.goToHomescreen {
            verifyHomeScreen()
        }
    }

    @Test
    fun testPageReloadAfterNetworkInterrupted() {
        val url = "example.com"

        activityTestRule.launchActivity(null)

        navigationToolbar {
        }.enterURLAndEnterToBrowser(url.toUri()) {}

        setNetworkConnection(false)
        IdlingRegistry.getInstance().unregister(networkDisconnectedIdlingResource)

        browserScreen {
        }.openThreeDotMenu {
        }.refreshPage {}
    }

    @Test
    fun testSignInPageWithNoNetworkConnection() {
        setNetworkConnection(false)

        activityTestRule.launchActivity(null)

        IdlingRegistry.getInstance().unregister(networkDisconnectedIdlingResource)

        homeScreen {
        }.openThreeDotMenu {
        }.openSettings {
        }.openTurnOnSyncMenu {
            tapOnUseEmailToSignIn()
            verifyUrl(
                "firefox.com",
                "$packageName:id/mozac_browser_toolbar_url_view",
                R.id.mozac_browser_toolbar_url_view
            )
        }
    }
}
