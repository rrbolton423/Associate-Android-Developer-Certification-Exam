import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.developer.taskmaker.AddTaskActivity;
import com.google.developer.taskmaker.MainActivity;
import com.google.developer.taskmaker.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UiTesting {

    // Create IntentTestRule for the MainActivity
    @Rule
    public IntentsTestRule<MainActivity> mMainActivityTestRule
            = new IntentsTestRule<>
            (MainActivity.class);

    // Create Instrumentation Test method
    @Test
    public void intentAddTask() {

        // Click the FloatingActionButton in the Activity
        onView(withId(R.id.fab)).perform(click());

        // Verify that the Intent was sent under test to the
        // AddTaskActivity
        intended(hasComponent(AddTaskActivity.class.getName()));
    }

}
