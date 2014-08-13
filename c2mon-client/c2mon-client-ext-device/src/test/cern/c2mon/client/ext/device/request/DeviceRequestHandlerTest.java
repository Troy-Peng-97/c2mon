package cern.c2mon.client.ext.device.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jms.JMSException;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cern.c2mon.client.jms.JmsProxy;
import cern.c2mon.shared.client.device.DeviceClassNameResponse;
import cern.c2mon.shared.client.device.DeviceClassNameResponseImpl;
import cern.c2mon.shared.client.device.TransferDevice;
import cern.c2mon.shared.client.device.TransferDeviceImpl;
import cern.c2mon.shared.client.request.ClientRequestResult;
import cern.c2mon.shared.client.request.JsonRequest;

/**
 * @author Justin Lewis Salmon
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:cern/c2mon/client/ext/device/config/c2mon-devicerequesthandler-test.xml" })
public class DeviceRequestHandlerTest {

  /** Component to test */
  @Autowired
  DeviceRequestHandler requestHandler;

  /** Mocked components */
  @Autowired
  JmsProxy jmsProxyMock;

  @Test
  public void testGetAllDeviceClassNames() throws JMSException {
    // Reset the mock
    EasyMock.reset(jmsProxyMock);

    Collection<ClientRequestResult> mockResponse = new ArrayList<>();
    mockResponse.add(new DeviceClassNameResponseImpl("test_device_class_name_1"));
    mockResponse.add(new DeviceClassNameResponseImpl("test_device_class_name_2"));

    // Expect the handler to send the request via the JmsProxy
    EasyMock.expect(jmsProxyMock.sendRequest(EasyMock.<JsonRequest<ClientRequestResult>> anyObject(), EasyMock.<String> anyObject(), EasyMock.anyInt()))
        .andReturn(mockResponse);

    // Setup is finished, need to activate the mock
    EasyMock.replay(jmsProxyMock);

    List<DeviceClassNameResponse> classNames = (List<DeviceClassNameResponse>) requestHandler.getAllDeviceClassNames();
    Assert.assertNotNull(classNames);
    Assert.assertTrue(classNames.size() == 2);
    Assert.assertTrue(classNames.get(0).getDeviceClassName().equals("test_device_class_name_1"));
    Assert.assertTrue(classNames.get(1).getDeviceClassName().equals("test_device_class_name_2"));

    // Verify that everything happened as expected
    EasyMock.verify(jmsProxyMock);
  }

  @Test
  public void testGetAllDevices() throws JMSException {
    // Reset the mock
    EasyMock.reset(jmsProxyMock);

    Collection<ClientRequestResult> mockResponse = new ArrayList<>();
    TransferDeviceImpl dti1 = new TransferDeviceImpl(1000L, "test_device_1", 1L);
    TransferDeviceImpl dti2 = new TransferDeviceImpl(2000L, "test_device_2", 1L);
    mockResponse.add(dti1);
    mockResponse.add(dti2);

    EasyMock.expect(jmsProxyMock.sendRequest(EasyMock.<JsonRequest<ClientRequestResult>> anyObject(), EasyMock.<String> anyObject(), EasyMock.anyInt()))
        .andReturn(mockResponse);

    // Setup is finished, need to activate the mock
    EasyMock.replay(jmsProxyMock);

    List<TransferDevice> devices = (List<TransferDevice>) requestHandler.getAllDevices("test_device_class_name_1");
    Assert.assertNotNull(devices);
    Assert.assertTrue(devices.get(0).getId().equals(dti1.getId()));
    Assert.assertTrue(devices.get(1).getId().equals(dti2.getId()));
    Assert.assertTrue(devices.get(0).getDeviceClassId().equals(dti1.getDeviceClassId()));
    Assert.assertTrue(devices.get(1).getDeviceClassId().equals(dti2.getDeviceClassId()));

    // Verify that everything happened as expected
    EasyMock.verify(jmsProxyMock);
  }
}
