<?xml version="1.0"?>

<!--

*** I fabricated a ContextRoot document. This transform assumes that the input document will be a context. 
	
*** Even though the documents are very similar to eachother, even in their element names, because they belong 
	to different namespaces, there seems to be no easy way of copying the original document to the target.

	This is not a big draw back, since when the transform gets only a little bit more complicated than this,
	copying trick would not work anyway. 

*** I made an assumption that in the output document there will always be a single MFPVendor element and single
	ScheduleProvider. 
	
	Multiple ScheduleClaims are allowed, and they are assumed to correspond to ScheduleLine elements from root.
	

*** 2002-08-09
	There was a schema change. Namely, eFormDocument element was moved from $root to $root.

*** 2002-08-13
	Fixes according to population instruction version 3.

*** 2002-08-15
	Inserted the SoapWrapper.

*** 2002-09-13
	The <document_date> and <document_time> elements are now filtered out if there are no data in these elements.

*** 2002-09-16
	The acc standard header template has been removed, namespace delcaration of the mfpScheduleNamespaceURI moved 
	from the document element to MFPSchedule element.
	
	new template - 'attachTime', will attach T00:00:00 to the incoming date if it doesnt have it already.
	claimant-date-of-birth and service-date are using this method.

*** 2002-09-16
	Added a global level variable: outputNamespace. please put the output document's namespace in this space to ensure
	optionally created elements have correct namespace.
	
	a few fields have been made optional - they will be created only when there are some data in them.
	
*** 2002-09-23
	MFPSchedule/MFPVendor/Schedule/ScheduleProvider/SchduleClaim/ScheduleLine/eFormContract/contact_acc_identifier
	has been made optional.

*** 2002-11-22
	Previously, It was assumed that all of the incoming ScheduleLines had same Provider and ClaimNumber - But it was 
	a wrong assumption (Which is a BadThing).
	
	Now this XSL will group all of the providers and schedule claims accordingly, constructing the proper tree structure.
	
*** 2002-11-27
	Added conversion of amount fields claimed_total_amount and $scheduleClaimedAmount to the format '0.00. Forces these values 
	to have exactly two decimal places. 
	
*** 2003-06-19
	Changed quite a few of the input node xpaths.
	
	
*** 2003-09-01
	Created another template, 'ToUpper', which will capitalize all English alphabet characters to theri
	respective upper case letters. Note, it wil NOT work with any other language.
	
	Capitalized elements are :
	
	Vendor ID
	root/Schedule/eFormVendor/vendor_acc_identifier
	Contract ID
	root/Schedule/ScheduleLine/ScheduleLineDetails/eFormContract/contract_a
	cc_identifier
	Claim Number
	root/Schedule/ScheduleLine/ScheduleLineClaim/medical_fees_number
	Approval Number
	root/Schedule/ScheduleLine/ScheduleLineClaim/approval_reference_number
	Provider ID
	root/Schedule/ScheduleLine/eFormProvider/provider_acc_identifier
	Service Codes
	root/Schedule/ScheduleLine/ScheduleLineDetails/eFormServiceLine/service
	_item_code
	
*** 2003-09-12
	Changed names of elements from eFormXXXX to mfpFormXXXX. This is to reflect the changes
	made in the schema 20030908. Note, that there was a fairly significant structural changes
	involving eFormInjuryLine, but fortunately that element was NOT a part of this transformation
	and has been disregarded.
	
*** 2003-11-18
	Added claimant second and third names.
	
***	2003-12-17
	Capitalized root number
	root/ScheduleHeader/eFormSchedule/schedule_number

***	2005-06-28
	RN2835 : upper case claimant_surname, format medicalFeesNumber		

***	2007-03-08
	AR212649: RN2835 did not format the medicalFeesNumber as the business wanted. Modified to strip special characters first
		and then check for old style numbers. Please note that there is a big assumption around this that EOS will not use
		10 digit claim numbers. 
	Reordering issues. Removed the fancy stuff that grouped the schedules by claim. This led to the reordering of the schedules 
		into MFP causing providers difficulty with providers reconciling payments. Note this could have been fixed in MFP 
		by using the schedule_line_number element when the message is loaded. If MFP implement this is may be "nicer" to 
		put the grouping back in.
***	2007-05-04: Small change to allow eForms to accept 11 digit GST numbers but not pass them through to MFP. Needed until
		MFP is updated. Note when this happens this entire file is replaced to include the new xsd xchema from 20070101

*** 2007-06-20: RN3260. Final release of the IRD Number change. Include new eForm to mfp schema. Remove the temp hack required to run with
		eForms at 11 chars and mfp at 10

*** 2010-11-11: TREE delivery - removed /ContextRoot/Document from root variable xpath 
-->
<xsl:transform 	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="xml" indent="yes" encoding="UTF-8" />
<xsl:strip-space elements="*" />

<xsl:variable name="root" select="CreateInvoiceFormRequest"/>

<xsl:variable name="outputNamespace" select="'http://xmlschema.acc.co.nz/claimmanagement/mfpSchedule20030908'"/>

<xsl:template match="/">
	<xsl:message>Matched root node</xsl:message>
	<xsl:message>Processor: <xsl:value-of select="system-property('xsl:vendor')"/></xsl:message>
	
	<MFPSchedule xmlns="http://xmlschema.acc.co.nz/claimmanagement/mfpSchedule20030908" 
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://xmlschema.acc.co.nz/claimmanagement/mfpSchedule20030908 http://xmlschema-dev.ds.acc.co.nz/claimmanagement/mfpSchedule20030908.xsd">

		<!-- ACC Standard Header from here -->
		<ACCStandardHeader/>

		<!-- Message Info from here -->
		<xsl:variable name="schedule" select="$root" />
		<MessageInfo>
			<eFormDocument>
				<document_reference><xsl:value-of select="$schedule/messageInfo/documentReference"/></document_reference>
				<xsl:call-template name="createOptional">
					<xsl:with-param name="name" select="'document_date'"/>
					<xsl:with-param name="data" select="$schedule/messageInfo/documentDate"/>
				</xsl:call-template>
				<xsl:call-template name="createOptional">
					<xsl:with-param name="name" select="'document_time'"/>
					<xsl:with-param name="data" select="$schedule/messageInfo/documentTime"/>
				</xsl:call-template>
				<document_source_app_version><xsl:value-of select="$schedule/messageInfo/documentSourceAppVersion"/></document_source_app_version>
			</eFormDocument>
			<document_gateway>E</document_gateway>
		</MessageInfo>
		
		<!-- MFP vendor -->
		<MFPVendor>
			<mfpFormVendor>
				<vendor_name><xsl:value-of select="$schedule/vendorName"/></vendor_name>
				<vendor_acc_identifier>
					<xsl:call-template name="ToUpper">
						<xsl:with-param name="text" select="$schedule/vendorId"/>
					</xsl:call-template>
				</vendor_acc_identifier>
				<vendor_gst_number>
					<xsl:call-template name="formatGSTNumber">
						<xsl:with-param name="text" select="$schedule/vendorGstNumber"/>
					</xsl:call-template>
				</vendor_gst_number>
				<vendor_phone_number><xsl:value-of select="$schedule/vendorPhoneNumber"/></vendor_phone_number>
			</mfpFormVendor>			

			<Schedule>
				<ScheduleHeader>
					<mfpFormSchedule>
						<schedule_number>
							<xsl:call-template name="ToUpper">
								<xsl:with-param name="text" select="$schedule/invoiceNumber"/>
							</xsl:call-template>
						</schedule_number>
						<provider_signed_date><xsl:value-of select="$schedule/invoiceDate"/></provider_signed_date>
						<lines_include_gst>Y</lines_include_gst>
						<schedule_comment><xsl:value-of select="$schedule/additionalComments"/></schedule_comment>
					</mfpFormSchedule>
				</ScheduleHeader>
				
				<!-- 
					This complicated(looking) logic is required since there is no multipass support in XSLT 1.0
					When XSLT2.0 comes out, this code can be simplified.

					Apply the template for all unique provider.
				-->
				<xsl:for-each select="$schedule/serviceDetails/serviceDetails">
					<xsl:call-template name="serviceDetails">
						<xsl:with-param name="position" select="position()"/>
					</xsl:call-template>
				</xsl:for-each>

				<ScheduleTrailer>
					<number_of_schedule_lines><xsl:value-of select="count($schedule/serviceDetails/serviceDetails)"/></number_of_schedule_lines>
					<schedule_claimed_amount><xsl:value-of select="format-number(sum($schedule/serviceDetails/serviceDetails/fee), '0.00')  "/></schedule_claimed_amount>
				</ScheduleTrailer>
			</Schedule>
		</MFPVendor>
		
		<!-- Standard Trailer -->
		<ACCStandardTrailer>
			<end_of_run_time><xsl:value-of select="$schedule/endOfRunDateTime"/></end_of_run_time>
			<end_of_run_description>End of Job</end_of_run_description>
		</ACCStandardTrailer>
	</MFPSchedule>

</xsl:template>


<!-- the matching node is /eForm/Schedule/ScheduleLines/ScheduleLine/Participant/eFormProvider -->
<xsl:template match="serviceDetails" name="serviceDetails">
	<xsl:param name="position"/>
	<xsl:variable name="schedule" select="$root" />
	<ScheduleProvider xmlns="http://xmlschema.acc.co.nz/claimmanagement/mfpSchedule20030908" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
					  xsi:schemaLocation="http://xmlschema.acc.co.nz/claimmanagement/mfpSchedule20030908 http://xmlschema-dev.ds.acc.co.nz/claimmanagement/mfpSchedule20030908.xsd">
		<mfpFormProvider>
			<provider_acc_identifier>
				<xsl:call-template name="ToUpper" >
					<xsl:with-param name="text" select="providerId"/>
				</xsl:call-template>
			</provider_acc_identifier>
			<provider_surname><xsl:value-of select="eFormProvider/provider_surname"/></provider_surname>
			<provider_given_names><xsl:value-of select="eFormProvider/provider_given_names"/></provider_given_names>
		</mfpFormProvider>

		<!-- iterate all of the ScheduleLine with current provider -->		
		<!-- the current ScheduleLine's medical fees number -->
		<ScheduleClaim>
			<medical_fees_number>
				<xsl:call-template name="formatMedicalFeesNumber">
					<xsl:with-param name="text" select="$schedule/claimNumber"/>
				</xsl:call-template>
			</medical_fees_number>
			<approval_reference_number>
				<xsl:call-template name="ToUpper">
					<xsl:with-param name="text" select="ScheduleLineClaim/approval_reference_number"/>
				</xsl:call-template>
			</approval_reference_number>
			<xsl:call-template name="createOptional">
				<xsl:with-param name="name" select="'accident_date'"/>
				<xsl:with-param name="data" select="$schedule/doa"/>
			</xsl:call-template>
			<eFormClaimant>
				<national_health_index><xsl:value-of select="$schedule/nhi"/></national_health_index>
				<claimant_first_name><xsl:value-of select="$schedule/firstName"/></claimant_first_name>
				<xsl:call-template name="createOptional">
					<xsl:with-param name="name" select="'claimant_second_name'"/>
					<xsl:with-param name="data" select="eFormClaimant/claimant_second_name"/>
				</xsl:call-template>
				<xsl:call-template name="createOptional">
					<xsl:with-param name="name" select="'claimant_third_name'"/>
					<xsl:with-param name="data" select="eFormClaimant/claimant_third_name"/>
				</xsl:call-template>
				<claimant_surname>
				   <xsl:call-template name="ToUpper">
					<xsl:with-param name="text" select="$schedule/surname"/>
				   </xsl:call-template>
				</claimant_surname>
				<claimant_date_of_birth><xsl:value-of select="$schedule/dob"/></claimant_date_of_birth>
			</eFormClaimant>
			<ScheduleLine>
				<xsl:variable name="contract_acc_identifier_upper">
					<xsl:call-template name="ToUpper">
						<xsl:with-param name="text" select="$schedule/contractNumber"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="createOptionalParent">
					<xsl:with-param name="parent" select="'mfpFormContract'"/>
					<xsl:with-param name="child" select="'contract_acc_identifier'"/>
					<xsl:with-param name="data" select="$contract_acc_identifier_upper"/>
				</xsl:call-template>	
				<eFormScheduleLine>
					<schedule_line_number><xsl:value-of select="$position"/></schedule_line_number>
					<service_date>
						<xsl:call-template name="attachTime">
							<xsl:with-param name="date" select="serviceDate"/>
						</xsl:call-template>
					</service_date>
					<facility_code><xsl:value-of select="facilityCode"/></facility_code>
					<loading_code>
						<xsl:call-template name="ToUpper">
							<xsl:with-param name="text" select="ScheduleLineDetails/eFormScheduleLine/loading_code"/>
						</xsl:call-template>
					</loading_code>									
					<claimed_total_amount><xsl:value-of select=" format-number( fee,'0.00')  "/></claimed_total_amount>
					<xsl:call-template name="createOptional">
						<xsl:with-param name="name" select="'claimed_hours'"/>
						<xsl:with-param name="data" select="format-number(totalFeeBasedOnTimeHours, '0')"/>
					</xsl:call-template>
					<xsl:call-template name="createOptional">
						<xsl:with-param name="name" select="'claimed_minutes'"/>
						<xsl:with-param name="data" select="format-number(totalFeeBasedOnTimeMinutes, '0')"/>
					</xsl:call-template>
					<xsl:call-template name="createOptional">
						<xsl:with-param name="name" select="'claimed_distance_travelled'"/>
						<xsl:with-param name="data" select="format-number(sum((serviceCodes/serviceCodes/distance)[number(.)=.]), '0')"/>
					</xsl:call-template>
					<xsl:call-template name="createOptional">
						<xsl:with-param name="name" select="'claimed_units'"/>
						<xsl:with-param name="data" select="format-number(sum((serviceCodes/serviceCodes/units)[number(.)=.]), '0.00')"/>
					</xsl:call-template>
					<schedule_line_comment><xsl:value-of select="serviceComments"/></schedule_line_comment>
				</eFormScheduleLine>
				<mfpFormServiceLine>
					<xsl:for-each select="serviceCodes/serviceCodes/code">
						<service_item_code>
							<xsl:call-template name="ToUpper">
								<xsl:with-param name="text" select="."/>
							</xsl:call-template>
						</service_item_code>
					</xsl:for-each>
				</mfpFormServiceLine>
			</ScheduleLine>
		</ScheduleClaim>
	</ScheduleProvider>
</xsl:template>

<xsl:template name="copyElement">
	
	<xsl:element name="{name()}" namespace="{$outputNamespace}">
		<xsl:copy-of select="@*"/>

		<xsl:for-each select="*">
			<xsl:call-template name="copyElement"/>
		</xsl:for-each>

		<xsl:if test="not(*)">
			<xsl:value-of select="."/>		
		</xsl:if>
	</xsl:element>
	
</xsl:template>

<!-- attaches the time 00:00:00 to a date. -->
<xsl:template name="attachTime">
<xsl:param name="date"/>
	<xsl:choose>
		<!-- contains T means contains time, since the dateTime format is yyyy-MM-ddThh:mm:ss.sss-->
		<xsl:when test="contains($date, 'T')">
			<xsl:value-of select="$date"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="concat($date, 'T00:00:00')"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<!-- creates a node only when the data is not an empty string.-->
<xsl:template name="createOptional">
<xsl:param name="name"/>
<xsl:param name="data"/>
	<xsl:if test="normalize-space($data) != ''">
		<xsl:element name="{$name}" namespace="{$outputNamespace}">
			<xsl:value-of select="$data"/>
		</xsl:element>
	</xsl:if>
</xsl:template>


<!-- creates a parent/child nodes only when data in the child node is not an empty string.-->
<xsl:template name="createOptionalParent">
<xsl:param name="parent"/>
<xsl:param name="child"/>
<xsl:param name="data"/>
	<xsl:if test="normalize-space($data) != ''">
		<xsl:element name="{$parent}" namespace="{$outputNamespace}">
		   <xsl:element name="{$child}" namespace="{$outputNamespace}">
			   <xsl:value-of select="$data"/>
		   </xsl:element>
		</xsl:element>
	</xsl:if>
</xsl:template>

<xsl:template name="ToUpper">
	<xsl:param name="text"/>
	<xsl:value-of select="translate($text, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
</xsl:template>


<!-- the following template is created by AliceChen on 22/06/2005 for medicalFeeNumber -->
<!-- format MedicalFeesNumber driven by RN2835 : uppercase, strip special characters and remove first two digits if it is 10 digits, etc -->
<!-- it is supposed to use the function matches to check against, however the function matches is not supported in the current version -->
<!-- Modified by roanne on 08/03/2007 to do the stripping first and then the checking for old numbers -->
<!-- Also put the special character part in a different template as it appears that other fields may require this in the future -->
<!-- 20070507 - Part of the M40 transition. Some providers are sending in ?? as the provider number. This is being stripped and blank -->
<!-- being sent to MFP which exceptions on this. So now if stripping special character leads to a blank - put in "Unknown".  -->
<xsl:template name="formatMedicalFeesNumber">
	<xsl:param name="text"/>
		<xsl:variable name="strippedMedFees" >
			<xsl:call-template name="removeSpecialChars">
				<xsl:with-param name="text1" select="$text"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
		<xsl:when test= "string-length($strippedMedFees) = 10 
		  and substring($strippedMedFees,1,1) >='0' and substring($strippedMedFees,1,1) &lt;='9' 
		  and substring($strippedMedFees,2,1) >='0' and substring($strippedMedFees,2,1) &lt;='9'
		  and substring($strippedMedFees,3,1) >='0' and substring($strippedMedFees,3,1) &lt;='9' 
		  and substring($strippedMedFees,4,1) >='0' and substring($strippedMedFees,4,1) &lt;='9'
		  and substring($strippedMedFees,5,1) >='0' and substring($strippedMedFees,5,1) &lt;='9'
		  and substring($strippedMedFees,6,1) >='0' and substring($strippedMedFees,6,1) &lt;='9'
		  and substring($strippedMedFees,7,1) >='0' and substring($strippedMedFees,7,1) &lt;='9'
		  and substring($strippedMedFees,8,1) >='0' and substring($strippedMedFees,8,1) &lt;='9'
		  and substring($strippedMedFees,9,1) >='0' and substring($strippedMedFees,9,1) &lt;='9'
		  and substring($strippedMedFees,10,1) >='0' and substring($strippedMedFees,10,1) &lt;='9'">
			<xsl:value-of select="substring($strippedMedFees, 3)"/>

		</xsl:when>
		<xsl:when test= "string-length($strippedMedFees) = 0">
			<xsl:value-of select="'BLANK'"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$strippedMedFees"/>
		</xsl:otherwise>
		</xsl:choose>	

</xsl:template>	

<!-- RN3260 + Production issue. 20070504. Two parts -->
<!-- Production issue: Providers are sending incorrect GST numbers that are NNNNNNNNNN. This is passing eForms but crashing in MFP -->
<!--    This change removes special characters and then checks the number is less than 8 (note this needs to be changed to 9 and -->
<!--	pushed out when MFP goes to the IRD number change and the schema version changes-->
<!-- RN3260 : eForms is going to be IRD# change before MFP, so need to make sure only nn-nnn-nnn numbers are sent through to MFP -->
<!--	even if the provider sends in nnn-nnn-nnn -->
<!-- 2007-06-20 : Keep the same error handling. If eForms gets a number it can cope with but mfp would fail - don't send it -->
<xsl:template name="formatGSTNumber">
	<xsl:param name="text"/>
		<xsl:variable name="strippedGSTNumber" >
			<xsl:call-template name="removeSpecialChars">
				<xsl:with-param name="text1" select="$text"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
		<xsl:when test= "string-length($strippedGSTNumber) > 9">
			<xsl:value-of select="''"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$strippedGSTNumber"/>
		</xsl:otherwise>
		</xsl:choose>	
</xsl:template>	


<xsl:template name="removeSpecialChars">
	<xsl:param name="text1"/>
		<xsl:value-of select="translate($text1, 'abcdefghijklmnopqrstuvwxyz-_/\|@`~!#$%^*_+=:;,.?&#40;&#41;&#91;&#93;&#123;&#125;&lt;&gt;&amp;', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
</xsl:template>	

</xsl:transform>

