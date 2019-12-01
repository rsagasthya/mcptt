# MCOP SDK version 0.2This is **MCOP SDK** (version 0.2 March 2019), the second release of the MCPTT (3GPP Release 13) compliant MCOP SDK.

<p align="center"><a href="https://mcopenplatform.org" target="_blank" rel="noopener noreferrer"><img src="https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/raw/master/images/logoMCOP_MD_w400px.png" alt="MCOP logo"></a></p>

The MCOP MCPTT Client comprises different elements connected by Android’s binder mechanism including the MCPTT Client GUI, the MCOP SDK (responsible of MCPTT protocols) and low level plugins to access SIM card, eMBMS, connectivity and configuration-oam to be deployed to get access to Mission Critical capabilities.

* Refer to [**MCOP SDK** Installation](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/blob/master/docs/MCOP_SDK_Installation.md) for detailed installation and testing steps. The [MCOP MCPTT App Development](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/blob/master/docs/MCOP_App_developing_steps.md) guide provides more info on app development using the MCOP SDK.This application uses [Doubango Framework](https://www.doubango.org/).Refer to [Doubango IMSDroid README](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/blob/master/docs/imsdroid_README.md) for additional IMS/SIP compatibility statements.

* The [**MCOP MCPTT Client**](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-MCPTT-Client.git/blob/master/README.md) that uses the MCOP SDK is also available to download.

License terms are defined in the [MCPTT Client](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/blob/master/docs/Licensing.md) and [Doubango](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/blob/master/docs/Licensing_Doubango.md) files. * For testing purposes, the [**MCOP Demo Platform**](https://demo.mcopenplatform.org/) is also available. Click on [**Request Access**](https://demo.mcopenplatform.org/reserve) and check the calendar there for an empty slot, and you'll receive credentials for five test users (with suffixes A to E). Update the profiles in the [**Provisioning Tool**](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/blob/master/docs/ProvisioningTool.md) with the provided configuration in order to test MCOP with our Online Testing Platform.


## The MCOP Project

<iframe src="https://player.vimeo.com/video/269823996?title=0" width="640" height="360" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe><p>link: <a href="https://vimeo.com/269823996">MCOP Video</a></p>

## DefinitionThe **MCOP** project comprises the definition and deployment of a technology neutral platform for MCPTT enabled apps for Public Safety in order to reduce the entry barriers and integration efforts due to the heterogeneity and complexity of the MCPTT ecosystem.The MCOP platform includes the so-called **MCOP Unified Open Application API** (Northbound API), supporting the interface between the MCPTT apps and the MCOP SDK and the Integration API, responsible for providing Southbound interface from the SDK to the OS-dependent low-level capabilities.![MCOP Architecture](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/raw/master/images/MCOParchitecture.png)* The **MCOP Unified Open Application API (MUOAPI)** provides a flexible interface for both MCPTT only clients and MCPTT capable multimedia apps to MCPTT communication primitives.* The **MCOP Open Source SDK** fully instantiates the MCOP Unified Open Application API by implementing 3GPP Rel’13 suite of protocols.* **MCOP Integration API** together with vendor and target technology-specific plugins ensures full MC-grade and future-proof capabilities to the MCOP apps by supporting eMBMS and low level LTE operations (while paving the way for future ProSE capabilities).

## Resources

**MUOAPI** definition, valid for any platform and any programming language:

* [**MUOAPI** Technology neutral definition](https://www.mcopenplatform.org/resources/MUOAPI_definition/)

Documentation:

* [**MCOP Unified Open Application API**](https://www.mcopenplatform.org/resources/MUOAPI/)## Feature listComplete feature list that the MCOP SDK 0.2 implements:

<table border='1' cellspacing='0' cellpadding='7'>
<blockquote><tr>
<blockquote><td><b>Features</b></td>
<td><b>Implemented</b></td>
<td><b>Notes</b></td>
</blockquote></tr>
<tr>
<blockquote><td>Private Call</td>
<td align="center"> ✔ </td>
<td> - </td>
</blockquote></tr>
<tr>
<blockquote><td>Prearranged Group Call</td>
<td align="center"> ✔ </td>
<td> - </td>
</blockquote></tr>
<tr>
<blockquote><td>Chat Group Calls</td>
<td align="center"> ✔ </td>
<td> New in v0.2 </td>
</blockquote></tr>
<tr>
<tr>
<blockquote><td>Emergency Group Calls</td>
<td align="center"> ✔ </td>
<td> New in v0.2 </td>
</blockquote></tr>
<tr>
<tr>
<blockquote><td>Emergency Private Calls</td>
<td align="center"> ✔ </td>
<td> New in v0.2 </td>
</blockquote></tr>
<tr>
<blockquote><td>Full-duplex Calls</td>
<td align="center"> ✔ </td>
<td> New in v0.2 </td>
</blockquote></tr>
<tr>
<tr>
<blockquote><td>Automatic Commencement Mode</td>
<td align="center"> ✔ </td>
<td> New in v0.2 </td>
</blockquote></tr>
<tr>
<tr>
<blockquote><td>Location</td>
<td align="center"> ✔ </td>
<td> - </td>
</blockquote></tr>
<tr>
<blockquote><td>IDMS</td>
<td align="center"> ✔ </td>
<td> New in v0.2 </td>
</blockquote></tr>
<tr>
<blockquote><td>CMS</td>
<td align="center"> ✔ </td>
<td> New in v0.2 </td>
</blockquote></tr>
<tr>
<tr>
<blockquote><td>GMS</td>
<td align="center"> ✔ </td>
<td> New in v0.2 </td>
</blockquote></tr>
<tr>
<blockquote><td>eMBMS</td>
<td align="center"> ✔ </td>
<td> Available if eMBMS plugin available in the device </td>
</blockquote></tr>
<tr>
<blockquote><td>Affiliation</td>
<td align="center"> ✔ </td>
<td> - </td>
</blockquote></tr>
<tr>
<blockquote><td>SIM Authentication</td>
<td align="center"> ✔ </td>
<td> Available trough plugins </td>
</blockquote></tr>
</blockquote><blockquote></table>
</br>


**Copyright (C) 2019, University of the Basque Country (UPV/EHU)**For more information, please visit [MCOP - Resources](https://www.mcopenplatform.org/mcop_resources/) or [MCOP webpage](https://www.mcopenplatform.org).
