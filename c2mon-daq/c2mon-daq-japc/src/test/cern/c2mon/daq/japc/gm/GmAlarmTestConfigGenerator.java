/*
 * $Id $
 * 
 * $Date$ $Revision$ $Author$
 * 
 * Copyright CERN ${year}, All Rights Reserved.
 */
package cern.c2mon.daq.japc.gm;

import static java.lang.String.format;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cern.c2mon.daq.japc.TestConfigGenerator;
import cern.c2mon.daq.japc.gm.GmJapcMessageHandler;
import cern.c2mon.daq.test.UseHandler;

@UseHandler(GmJapcMessageHandler.class)
public class GmAlarmTestConfigGenerator extends TestConfigGenerator {

    static final String dataTagTemplate = "         <DataTag id=\"%d\" name=\"%s\" control=\"false\">\n"
            + "            <data-type>%s</data-type>\n"
            + "            <DataTagAddress>\n"
            + "                <HardwareAddress class=\"ch.cern.tim.shared.datatag.address.impl.JAPCHardwareAddressImpl\">\n"
            + "                   <protocol>rda</protocol>\n" + "                   <service>rda</service>\n"
            + "                   <device-name>%s</device-name>\n"
            + "                   <property-name>%s</property-name>\n" + "                 </HardwareAddress>\n"
            + "                 <time-to-live>3600000</time-to-live>\n" + "                 <priority>2</priority>\n"
            + "                 <guaranteed-delivery>false</guaranteed-delivery>\n" + "            </DataTagAddress>\n"
            + "         </DataTag>\n";

    private String createTagXML(long tagId, String tagName, String tagType, String deviceName, String propertyName) {
        return String.format(dataTagTemplate, tagId + 1000, tagName, tagType, deviceName, propertyName);
    }

    @Override
    protected String getDataTags() {

        StringBuilder str = new StringBuilder();

        List<String> devprops = null;

        try {
            devprops = load();
            long tagNo = 1;
            for (String dp : devprops) {
                String[] device_property = dp.split("/");
                if (device_property.length != 2)
                    out.println(dp);
                String tagname = device_property[0] + ":" + device_property[1] + ":VALUE";
                str.append(createTagXML(tagNo++, tagname, "Integer", device_property[0], device_property[1]));
            }
            out.println(format("number of DataTags generated: %d", tagNo - 1));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return str.toString();
    }

    private List<String> load() throws IOException {
        List<String> list = new ArrayList<String>();

        InputStream is = null;

        try {
            is = this.getClass().getResourceAsStream("gm.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }

        } finally {
            if (is != null)
                is.close();
        }

        return list;
    }

    @Override
    protected String getFileDescription() {
        StringBuilder descr = new StringBuilder("  this configuration has been created for the JAPC GM devices\n");
        return descr.toString();
    }

    @Override
    protected String getOutputFileName() {
        return String.format(outputFileNameTemplate, processName, "gm");
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TestConfigGenerator generator = new GmAlarmTestConfigGenerator();
        generator.generateConfXML();
    }
}
