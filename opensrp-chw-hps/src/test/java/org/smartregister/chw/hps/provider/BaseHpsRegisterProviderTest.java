package org.smartregister.chw.hps.provider;

import static org.mockito.Mockito.validateMockitoUsage;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.hps.R;
import org.smartregister.chw.hps.fragment.BaseHpsRegisterFragment;
import org.smartregister.chw.hps.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseHpsRegisterProviderTest {
    @Mock
    public CommonPersonObjectClient commonPersonObjectClient;
    @Mock
    public View.OnClickListener listener;
    @Mock
    public HpsRegisterProvider.RegisterViewHolder viewHolder;
    @Mock
    private HpsRegisterProvider testRegisterProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

    @Test
    public void updateClients() {
        Assert.assertNull(testRegisterProvider.updateClients(null, null, null, null));
    }

    @Test
    public void newFormLauncher() {
        Assert.assertNull(testRegisterProvider.newFormLauncher(null, null, null));
    }

    @Test
    public void checkInflater() {
        Assert.assertNull(testRegisterProvider.inflater());
    }

    @Test
    public void checkFooter() {
        Assert.assertNotNull(testRegisterProvider.isFooterViewHolder(null));
    }

    @Test
    public void checkFooterCreation() {
        Assert.assertNull(testRegisterProvider.createFooterHolder(null));
    }

    @Test
    public void checkHolderCreation() {
        Assert.assertNull(testRegisterProvider.createViewHolder(null));
    }

    @Test
    public void isAncClosed() throws Exception {
        Resources resources = Mockito.mock(Resources.class);
        Activity activity = Mockito.mock(Activity.class);
        HpsRegisterProvider provider = Mockito.spy(new HpsRegisterProvider(activity, listener, listener, null));
        Map<String, String> map = new HashMap<>();
        map.put("is_anc_closed", "0");
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(commonPersonObjectClient.getColumnmaps()).thenReturn(map);
        Assert.assertEquals(resources.getString(R.string.anc_string), Whitebox.invokeMethod(provider, "updateMemberGender", commonPersonObjectClient));
    }

    @Test
    public void isPncClosed() throws Exception {
        Resources resources = Mockito.mock(Resources.class);
        Activity activity = Mockito.mock(Activity.class);
        HpsRegisterProvider provider = Mockito.spy(new HpsRegisterProvider(activity, listener, listener, null));
        Map<String, String> map = new HashMap<>();
        map.put("is_pnc_closed", "0");
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(commonPersonObjectClient.getColumnmaps()).thenReturn(map);
        Assert.assertEquals(resources.getString(R.string.pnc_string), Whitebox.invokeMethod(provider, "updateMemberGender", commonPersonObjectClient));
    }

    @Test
    public void updateMemberGender() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Resources resources = Mockito.mock(Resources.class);
        HpsRegisterProvider provider = new HpsRegisterProvider(activity, listener, listener, null);
        Map<String, String> map = new HashMap<>();
        map.put(DBConstants.KEY.GENDER, "Male");

        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(commonPersonObjectClient.getColumnmaps()).thenReturn(map);
        Assert.assertEquals(resources.getString(R.string.male), Whitebox.invokeMethod(provider, "updateMemberGender", commonPersonObjectClient));
    }


    @Test
    public void rowBackgroundOpensTheClientProfile() {
        Activity activity = Mockito.mock(Activity.class);
        Resources resources = Mockito.mock(Resources.class);
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(resources.getString(R.string.male)).thenReturn("Male");
        HpsRegisterProvider provider = new HpsRegisterProvider(activity, listener, listener, Collections.emptySet());
        Map<String, String> columns = new HashMap<>();
        columns.put(DBConstants.KEY.FIRST_NAME, "Test");
        columns.put(DBConstants.KEY.LAST_NAME, "Client");
        columns.put(DBConstants.KEY.DOB, "1990-01-01");
        columns.put(DBConstants.KEY.GENDER, "Male");
        Mockito.when(commonPersonObjectClient.getColumnmaps()).thenReturn(columns);
        viewHolder.patientName = Mockito.mock(TextView.class);
        viewHolder.textViewGender = Mockito.mock(TextView.class);
        viewHolder.textViewVillage = Mockito.mock(TextView.class);
        viewHolder.patientColumn = Mockito.mock(View.class);
        viewHolder.registerColumns = Mockito.mock(View.class);
        viewHolder.dueButton = Mockito.mock(Button.class);

        provider.getView(null, commonPersonObjectClient, viewHolder);

        ArgumentCaptor<View.OnClickListener> click = ArgumentCaptor.forClass(View.OnClickListener.class);
        Mockito.verify(viewHolder.registerColumns).setOnClickListener(click.capture());
        click.getValue().onClick(viewHolder.registerColumns);
        Mockito.verify(viewHolder.patientColumn).performClick();
        Mockito.verify(viewHolder.patientColumn).setTag(R.id.VIEW_ID, BaseHpsRegisterFragment.CLICK_VIEW_NORMAL);
    }

    @Test(expected = Exception.class)
    public void getView() throws Exception {
        testRegisterProvider.getView(null, null, viewHolder);
        PowerMockito.when(testRegisterProvider, "populatePatientColumn", commonPersonObjectClient, viewHolder).then(DoesNothing.doesNothing());
        PowerMockito.verifyPrivate(testRegisterProvider).invoke("populatePatientColumn", commonPersonObjectClient, viewHolder);
    }

}
