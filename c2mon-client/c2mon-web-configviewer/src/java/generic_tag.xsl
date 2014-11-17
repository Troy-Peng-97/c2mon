<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!--  link variables  -->
  <xsl:variable name="base_url">../</xsl:variable>
  <xsl:variable name="alarm_url">alarmviewer/</xsl:variable>
  <xsl:variable name="command_url">commandviewer/</xsl:variable>
  <xsl:variable name="datatag_url">tagviewer/</xsl:variable>
  <xsl:variable name="process_xml_url">process/xml/</xsl:variable>
  <xsl:variable name="alarm_xml_url">alarmviewer/xml/</xsl:variable>
  <xsl:variable name="command_xml_url">commandviewer/xml/</xsl:variable>
  <xsl:variable name="report_xml_url">configloader/progress/finalReport/xml/</xsl:variable>
  
  <xsl:variable name="help_alarm_url">http://oraweb.cern.ch/pls/timw3/helpalarm.AlarmForm?p_alarmid=</xsl:variable>
  

  <!--  leave the paragraphs untouched -->
  <xsl:template match="p">
    <xsl:copy-of select="." />
  </xsl:template>

  <xsl:template match="ClientDataTag">


    <p class="tagName">
      <xsl:value-of select="tagName" />
      (
      <xsl:value-of select="@id" />
      )
    </p>
    <table class="inline">
      <th colspan="4">ClientDataTag</th>

      <tr>
        <td class="highlight bold">Tag id </td>
        <td>
          <xsl:value-of select="@id" />
        </td>
        <td class="highlight bold">Tag name </td>
        <td>
          <xsl:value-of select="tagName" />
        </td>
      </tr>

      <xsl:for-each
        select="*[not(local-name() = 'alarms' or local-name() = 'tagName')]">

        <xsl:if test="position() mod 2 = 1">
          <xsl:text disable-output-escaping='yes'>&lt;TR></xsl:text>
          <TD class="highlight bold"><xsl:value-of select="local-name()"/></TD>
          <TD width="25%"><xsl:value-of select="."/></TD>
        </xsl:if>
        
        <xsl:if  test="position() mod 2 = 0">
          <TD class="highlight bold"><xsl:value-of select="local-name()"/></TD>
          <TD width="25%"><xsl:value-of select="."/></TD>    
          <xsl:text disable-output-escaping='yes'>&lt;/TR></xsl:text>
        </xsl:if>
        
      </xsl:for-each>
      
    </table>
    <xsl:apply-templates select="alarms"/>
  </xsl:template>
  
  <xsl:template match="alarms">
    <xsl:apply-templates select="alarmValue"/>
  </xsl:template>

  <!--  process the XML element TagConfig - take missing information from the element ClientDataTag -->
  <xsl:template match="TagConfig">
    <table class="inline">
      <th colspan="4">Tag Configuration</th>
      <tr>
        <td class="highlight bold">Tag id </td>
        <td><xsl:value-of select="@id"/></td>
        <td class="highlight bold">Tag name </td>
        <td><xsl:value-of select="../ClientDataTag/tagName"/></td>
      </tr>
      <tr>
        <td class="highlight bold">Description </td>
        <td><xsl:value-of select="../ClientDataTag/description"/></td>
        <td class="highlight bold"> Mode </td>
        <td><xsl:value-of select="../ClientDataTag/mode"/></td>
      </tr>
      <tr>
        <td class="highlight bold"> JMS Topic name </td>
        <td><xsl:value-of select="../ClientDataTag/topicName"/></td>
        <td class="highlight bold"> Data Type </td>
        <td><xsl:value-of select="../ClientDataTag/tagValue/@class"/></td>
      </tr>
      
      <xsl:for-each select="*[not(local-name() = 'alarmIds' or local-name() = 'hardwareAddress')]">

        <xsl:if test="position() mod 2 = 1">
          <xsl:text disable-output-escaping='yes'>&lt;TR></xsl:text>
          <TD class="highlight bold"><xsl:value-of select="local-name()"/></TD>
          <TD width="25%"><xsl:value-of select="."/></TD>
        </xsl:if>
        
        <xsl:if  test="position() mod 2 = 0">
          <TD class="highlight bold"><xsl:value-of select="local-name()"/></TD>
          <TD width="25%"><xsl:value-of select="."/></TD>    
          <xsl:text disable-output-escaping='yes'>&lt;/TR></xsl:text>
        </xsl:if>
        
      </xsl:for-each>
      
      <tr>
        <td class="highlight bold"> Alarms </td>
        <td>
          <xsl:for-each select="alarmIds">
            <a href="{$base_url}{$alarm_url}{long}/"><xsl:value-of select="long"/></a>
          </xsl:for-each>
        </td>
      </tr>
      
      <tr>
        <td class="highlight bold align_center" colspan="4" >Hardware Address</td>
      </tr>
      <tr>
        <td colspan="4"><xsl:value-of select="hardwareAddress"/></td>
      </tr>
      
      <!--
      <tr>
        <td class="highlight bold" style="background:red;"> Publications - no JAPC and DIP</td>
        <td><xsl:value-of select="publications"/></td>
      </tr>
    -->
    
  </table>
  
</xsl:template>

<!-- process the XML element AlarmValue -->

<!-- page : alarmviewer -->
<xsl:template match="AlarmValue | alarmValue">
  
  <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>
  <html>
    <head>
      <title>Configuration viewer</title>
      <link rel="stylesheet" type="text/css" href="../css/bootstrap/bootstrap.css" />
      <link rel="stylesheet" type="text/css" href="../css/c2mon.css"></link>
      <link rel="stylesheet" type="text/css" href="../css/web-config-viewer.css"></link>
      <link rel="stylesheet" type="text/css" href="../css/buttons.css"></link>
      
      <script type="text/javascript" src="../js/jquery/jquery.js"></script>
      <script type="text/javascript" src="../js/bottom_panel.js"></script>
    </head>
    <body>
      
      <div class="page-header"> 
        <h2>
          <xsl:value-of select="faultFamily"/>
          :<xsl:value-of select="faultMemeber"/>
          :<xsl:value-of select="faultCode"/>
        </h2>
      </div>
      <div class="links" style="margin-bottom:5%;">
          <A style="display:inline;float:left;" href="../" 
        class="large blue awesome xml_button">    
        <i class="icon-home"></i> Home
        </A>
        <A href="{$base_url}{$alarm_xml_url}{@id}/" 
          class="large blue awesome xml_button" target="_blank">View Alarm XML >>
        </A>  
        
        <A href="{$help_alarm_url}{@id}" 
          class="large red awesome xml_button" target="_blank">View Help Alarm >>
        </A>    
      </div>
      <table class="inline">
        <th colspan="4">Alarm Value</th>
        
        <tr>
          <td class="highlight bold">Alarm id</td>
          <td><xsl:value-of select="@id"/></td>  
          <td class="highlight bold">Class</td>
          <td><xsl:value-of select="@class"/></td>  
        </tr>
        
        <tr>
          <td class="highlight bold"> DataTag </td>
          <td >
            <a href="{$base_url}{$datatag_url}{tagId}"><xsl:value-of select="tagId"/></a>
          </td>
          <td class="highlight bold"> State </td>
          <td>
            <xsl:choose>
              <xsl:when test="active='false'">
                <xsl:text>TERMINATED</xsl:text>
              </xsl:when>
              <xsl:when test="active='true'">
                <xsl:text>ACTIVE</xsl:text>
              </xsl:when>
            </xsl:choose>
          </td>
        </tr>
        
        <xsl:for-each select="*[not((local-name() = 'tagId') or local-name() = 'active')]">
          
          <xsl:if  test="position() mod 2 = 0">
            <TD class="highlight bold"><xsl:value-of select="local-name()"/></TD>
            <TD width="25%"><xsl:value-of select="."/></TD>    
            <xsl:text disable-output-escaping='yes'>&lt;/TR></xsl:text>
          </xsl:if>
          
          <xsl:if test="position() mod 2 = 1">
            <xsl:text disable-output-escaping='yes'>&lt;TR></xsl:text>
            <TD class="highlight bold"><xsl:value-of select="local-name()"/></TD>
            <TD width="25%"><xsl:value-of select="."/></TD>
          </xsl:if>  
          
        </xsl:for-each>
        
      </table>
      
    </body>
  </html>
</xsl:template>


<!-- process the XML element ClientCommandTagImpl -->
<xsl:template match="ClientCommandTag">
  
  <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>
  <html>
    <head>
      <title>Configuration viewer</title>
      <link rel="stylesheet" type="text/css" href="../css/bootstrap/bootstrap.css" />
      <link rel="stylesheet" type="text/css" href="../css/c2mon.css"></link>
      <link rel="stylesheet" type="text/css" href="../css/web-config-viewer.css"></link>
      <link rel="stylesheet" type="text/css" href="../css/buttons.css"></link>
      
      <script type="text/javascript" src="../js/jquery/jquery.js"></script>
      <script type="text/javascript" src="../js/bottom_panel.js"></script>
    </head>
    <body>
      
      <div class="page-header">
        <h2>
          <xsl:value-of select="@name"/>
          (<xsl:value-of select="@id"/>)
        </h2>
      </div>
      
      <div class="links" style="margin-bottom:2%;"> 
        <A style="display:inline;float:left;" href="../" 
        class="large blue awesome xml_button">    
        <i class="icon-home"></i> Home
        </A>
        <A href="{$base_url}{$command_xml_url}{@id}/" 
          class="large blue awesome xml_button" target="_blank">View Command XML >>
        </A>    
      </div>
      <div class="column" style="margin-top:5%;">
        <table class="inline">
          <th colspan="2">Command Tag</th>
          <tr>
            <td class="highlight bold"> Command id </td>
            <td class=""><xsl:value-of select="@id"/></td>
          </tr>
          <tr>
            <td class="highlight bold"> Name </td>
            <td class=""><xsl:value-of select="@name"/></td>
          </tr>

          <xsl:for-each select="*[not(local-name() = 'HardwareAddress')]">
            <TR>
              <TD class="highlight bold"><xsl:value-of select="local-name()"/></TD>
              <TD><xsl:value-of select="."/></TD>
            </TR>
          </xsl:for-each>
        
        </table>
        
        <xsl:template match="HardwareAddress">
            <table class="inline">
              <th colspan="2">HardwareAddress</th>
              <xsl:for-each select="HardwareAddress/*">
                <TR>
                  <TD class="bold"><xsl:value-of select="local-name()"/></TD>
                  <TD><xsl:value-of select="."/></TD>
                </TR>
              </xsl:for-each>
        
            </table>
        </xsl:template>
      </div>
      
    </body>
  </html>
</xsl:template>

<!-- process the XML element ConfigurationReport -->
<xsl:template match="ConfigurationReport">
  
  <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>
  <html>
    <head>
      <title>Configuration viewer</title>
      <link rel="stylesheet" type="text/css" href="../../../css/bootstrap/bootstrap.css" />
<!--       <link rel="stylesheet" type="text/css" href="../../../css/c2mon.css"></link> -->
<!--       <link rel="stylesheet" type="text/css" href="../../../css/web-config-viewer.css"></link>
      <link rel="stylesheet" type="text/css" href="../../../css/buttons.css"></link> -->

      <script type="text/javascript" src="../../../js/jquery/jquery.js"></script>
      <script type="text/javascript" src="../../../js/bootstrap/bootstrap.js"></script>
      <script type="text/javascript" src="../../../js/bottom_panel.js"></script>
    </head>
    <body style="overflow-y: scroll;">
      
      <div class="container-fluid" style="padding-left:150px; padding-right:150px;">
      <div class="row">
      
      <p class="tagName"> 
        <a href="../../{$base_url}{$report_xml_url}{id}/"  style="margin-top: 20px;"
          class="btn btn-default btn-large pull-right" target="_blank">View Configuration Report XML >>
        </a>    
      </p>
      <p>
        <h2>Overview</h2>
        <table class="table table-striped table-bordered">
          <tr>
            <th class="bold">Identifier</th>
            <td><xsl:value-of select="id" /></td>
          </tr>
          <tr>
            <th class="bold">Name</th>
            <td><xsl:value-of select="name" /></td>
          </tr>
          <tr>
            <th class="bold">Applied by</th>
            <td><xsl:value-of select="user" /></td>
          </tr>
          <tr>
            <th class="bold">Applied on</th>
            <td><xsl:value-of select="timestamp" /></td>
          </tr>
          <tr>
            <th class="bold">Status</th>
            <xsl:choose>
              <xsl:when test="status='OK'">
                <td class="success"><xsl:value-of select="status" /></td>
              </xsl:when>  
              <xsl:when test="status='WARNING' or status='RESTART'">
                <td class="warning"><xsl:value-of select="status" /></td>
              </xsl:when>
              <xsl:otherwise>
                <td class="danger"><xsl:value-of select="status" /></td>
              </xsl:otherwise>
            </xsl:choose>
          </tr>
          <tr>
            <th class="highlight bold">Message</th>
            <td><xsl:value-of select="status-description" /></td>
          </tr>
          <tr>
            <th class="highlight bold">DAQs to reboot</th>
            <td><xsl:value-of select="daq-reboot" /></td>
          </tr>
        </table>
      </p>
      <xsl:apply-templates select="ConfigurationElementReports"/>
      
      </div>
      </div>
    </body>
  </html>
</xsl:template>


<xsl:template match="ConfigurationElementReports">
  <hr/>
  <p>
    <h2>Detailed Report</h2>
    <table class="table table-striped table-bordered">
    <thead>
      <tr>
        <th class="col-md-1">Action</th>
        <th class="col-md-1">Entity</th>
        <th class="col-md-1">Id</th>
        <th class="col-md-1">Status</th>
        <th >Report</th>
      </tr>
    </thead>
    <tbody>
      <xsl:for-each select="ConfigurationElementReport">
        <tr>
          <td><xsl:value-of select="action" /></td>
          <td><xsl:value-of select="entity" /></td>
          <td><xsl:value-of select="id" /></td>
          <xsl:choose>
            <xsl:when test="status='OK'">
              <td class="success"><xsl:value-of select="status" /></td>
            </xsl:when>  
            <xsl:when test="status='WARNING' or status='RESTART'">
              <td class="warning"><xsl:value-of select="status" /></td>
            </xsl:when>  
            <xsl:otherwise>
              <td class="danger"><xsl:value-of select="status" /></td>
            </xsl:otherwise>
          </xsl:choose>

          <td>
          
            <button type="button" class="btn btn-default" data-toggle="collapse" data-target="#collapseme-{action}-{id}">
              Click to expand
            </button>
            
            <div id="collapseme-{action}-{id}" class="collapse out">
              <xsl:if test="status-message!='' ">
                <div style="white-space: pre-wrap;">
                  <xsl:value-of select="status-message" />
                </div>
              </xsl:if>
              <ul class="list-unstyled">
                <xsl:apply-templates select="sub-reports"/>
              </ul>
            </div>

          </td>
        </tr>
      </xsl:for-each>
      </tbody>
    </table>
  </p>
</xsl:template>

<xsl:template match="sub-reports">

    <xsl:for-each select="ConfigurationElementReport">
      <li style="white-space: pre-wrap;">
        <xsl:value-of select="action" /> -  
        <xsl:value-of select="entity" /> - 
        <xsl:value-of select="id" /> -  
        <xsl:value-of select="status"/> - 
        
        <xsl:if test="status-message!=''">
          <xsl:value-of select="status-message"/>
        </xsl:if>
      </li>
      <xsl:apply-templates select="sub-reports"/>
    </xsl:for-each>

</xsl:template>
<!-- process the XML element ProcessConfiguration -->
<!-- DAQ page -->
<xsl:template match="ProcessConfiguration">
  
  <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>
  <html>
    <head>
      <title>Configuration viewer</title>
      <link rel="stylesheet" type="text/css" href="../css/bootstrap/bootstrap.css" />
      <link rel="stylesheet" type="text/css" href="../css/c2mon.css"></link>
      <link rel="stylesheet" type="text/css" href="../css/web-config-viewer.css"></link>
      <link rel="stylesheet" type="text/css" href="../css/buttons.css"></link>

      <script type="text/javascript" src="../js/jquery/jquery.js"></script>
      <script type="text/javascript" src="../js/bottom_panel.js"></script>
      
      <script type="text/javascript">
          $(document).ready(function(){
            function getProcessName(){
              var url = window.location.pathname;
              return url.substring(url.lastIndexOf("/") + 1, url.length);
            }
  
            $("#process_name").append(getProcessName());
            var urlForRawXmlView = document.getElementById('xml_butt');
            urlForRawXmlView.href = urlForRawXmlView + getProcessName();
          });
        </script>
      
    </head>
    <body>
      
      <div id="top"></div>
      <div class="page-header"> 
        <h2>DAQ Process XML Viewer</h2> 
      </div>  
      <div class="links" style="margin-bottom:2%;">       
        <A style="display:inline;float:left;" href="../" 
        class="large blue awesome xml_button">    
        <i class="icon-home"></i> Home
        </A>
        <A href="{$base_url}{$process_xml_url}" 
          id="xml_butt" class="large blue awesome xml_button" target="_blank">View as XML >>
        </A>
      </div>  
      <br/><br/>
      <p id="process_name">Process: </p>

    <div class="column">
      <table class="inline">
        <th colspan="2">Process Configuration</th>
        <tr>
          <td class="bold"> process id </td>
          <td><xsl:value-of select="@process-id"/></td>
        </tr>
        <tr>
          <td class="bold"> type </td>
          <td><xsl:value-of select="@type"/></td>
        </tr>

        <xsl:for-each select="*[not(local-name() = 'EquipmentUnits')]">
          <TR>
            <TD class="bold"><xsl:value-of select="local-name()"/></TD>
            <TD><xsl:value-of select="."/></TD>
          </TR>
        </xsl:for-each>
        
      </table>
    </div>
    <xsl:apply-templates select="EquipmentUnits"/>
    
  </body>
</html>
</xsl:template>

<xsl:template match="EquipmentUnits">

  <div class="message"> Scroll to <a href="#top"> top</a></div>
  
  <xsl:apply-templates select="EquipmentUnit"/>
</xsl:template>


<xsl:template match="EquipmentUnit">
  <p class="tagName"> 
    <a name="{@name}"> <xsl:value-of select="@name"/> </a> : (<xsl:value-of select="@id"/>)
  </p>
  <div class="column">
    <table class="inline">
      <th colspan="2">EquipmentUnit</th>
      <tr>
        <td class="bold">  id </td>
        <td><xsl:value-of select="@id"/></td>
      </tr>
      <tr>
        <td class="bold"> name </td>
        <td><xsl:value-of select="@name"/></td>
      </tr>

      <xsl:for-each select="*[not(local-name() = 'DataTags' or local-name() = 'SubEquipmentUnits' 
        or local-name() = 'CommandTags')]">
        <TR>
          <TD class="bold"><xsl:value-of select="local-name()"/></TD>
          <TD><xsl:value-of select="."/></TD>
        </TR>
      </xsl:for-each>
      
    </table>
  </div>
  <xsl:apply-templates select="SubEquipmentUnits"/>
  <xsl:apply-templates select="DataTags"/>
  <xsl:apply-templates select="CommandTags"/>
</xsl:template>

<xsl:template match="SubEquipmentUnits">
  <xsl:apply-templates select="SubEquipmentUnit"/>
</xsl:template>

<xsl:template match="SubEquipmentUnit">
  <p class="tagName">
    <a name="{@name}"> <xsl:value-of select="@name"/> </a> : (<xsl:value-of select="@id"/>)
  </p>
  <div class="column">
    <table class="inline">
      <th colspan="2">SubEquipmentUnit</th>
      <tr>
        <td class="bold">  id </td>
        <td><xsl:value-of select="@id"/></td>
      </tr>
      <tr>
        <td class="bold"> name </td>
        <td><xsl:value-of select="@name"/></td>
      </tr>

      <xsl:for-each select="*">
        <TR>
          <TD class="bold"><xsl:value-of select="local-name()"/></TD>
          <TD><xsl:value-of select="."/></TD>
        </TR>
      </xsl:for-each>
      
    </table>
  </div>
</xsl:template>

<xsl:template match="CommandTags">
  <xsl:apply-templates select="CommandTag"/>
</xsl:template>

<xsl:template match="CommandTag">
  <p class="tagName"> 
    <a href="{$base_url}{$command_url}{@id}/"><xsl:value-of select="@name"/>:(<xsl:value-of select="@id"/>)</a>&#160;
  </p>
  <p>
    This CommandTag belongs to Equipment 
    <a href="#{../../@name}"><xsl:value-of select="../../@name" /></a> 
  </p>
  <div class="column">
    <table class="inline">
      <th colspan="2">CommandTag</th>
      
      <tr>
        <td class="bold">  id </td>
        <td><xsl:value-of select="@id"/></td>
      </tr>
      <tr>
        <td class="bold">  name </td>
        <td><xsl:value-of select="@name"/></td>
      </tr>

      <xsl:for-each select="*">
        
        <TR>
          <TD class="bold"><xsl:value-of select="local-name()"/></TD>
          <TD><xsl:value-of select="."/></TD>
        </TR>
      </xsl:for-each>
      
    </table>
    <xsl:apply-templates select="HardwareAddress"/>
  </div>
</xsl:template>

<xsl:template match="DataTags">
  <xsl:apply-templates select="DataTag"/>
</xsl:template>

<xsl:template match="DataTag">
  <p class="tagName"> 
    <a href="{$base_url}{$datatag_url}{@id}"><xsl:value-of select="@name"/>:(<xsl:value-of select="@id"/>)</a>&#160;
  </p>
  <p>
    This DataTag belongs to Equipment 
    <a href="#{../../@name}"><xsl:value-of select="../../@name" /></a> 
  </p>
  <div class="column">
    <table class="inline">
      <th colspan="2">DataTag</th>
      <tr>
        <td class="bold">  id </td>
        <td><xsl:value-of select="@id"/></td>
      </tr>
      <tr>
        <td class="bold">  name </td>
        <td><xsl:value-of select="@name"/></td>
      </tr>

      <xsl:for-each select="*[not(local-name() = 'DataTagAddress')]">
        <TR>
          <TD class="bold"><xsl:value-of select="local-name()"/></TD>
          <TD><xsl:value-of select="."/></TD>
        </TR>
      </xsl:for-each>
      
    </table>
    <xsl:apply-templates select="DataTagAddress"/>
  </div>
</xsl:template>

<xsl:template match="DataTagAddress">
  <p class="tagName"></p>
  <div class="column">
    <table class="inline">
      <th colspan="2">DataTagAddress</th>

      <xsl:for-each select="*[not(local-name() = 'HardwareAddress')]">
        
        <TR>
          <TD class="bold"><xsl:value-of select="local-name()"/></TD>
          <TD><xsl:value-of select="."/></TD>
        </TR>
      </xsl:for-each>
    </table>
    
    <xsl:apply-templates select="HardwareAddress"/>
  </div>
  
</xsl:template>

<xsl:template match="HardwareAddress">
  <p class="tagName"></p>
  <div class="column">
    <table class="inline">
      <th colspan="2">HardwareAddress</th>

      <xsl:for-each select="*">
        <TR>
          <TD class="bold"><xsl:value-of select="local-name()"/></TD>
          <TD><xsl:value-of select="."/></TD>
        </TR>
      </xsl:for-each>

    </table>
  </div>
</xsl:template>


</xsl:stylesheet>