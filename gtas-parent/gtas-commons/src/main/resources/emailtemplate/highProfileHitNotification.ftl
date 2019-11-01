<head>
    <title>Generated Hits</title>
</head>
<body>
    <#if !hits[0].hitEmailSenderDTO??>
        <p>This is a system generated email caused by elevated risk categories for the following <font color="red"><b>${hits?size}</b></font> passenger(s):</p>
    </#if>
    <#list hits as hit>
        <#if hits?size != 1>
            <font color="red"><b>#${hit?index+1}</b></font>
        </#if>
        <div style="margin-left: 30px;">
            <#if hit.note?has_content>
                <b><font color="red"><span class="il">NOTES</span></font>:</b><br>
                <p style="margin-left: 30px;">${hit.note}</p>
            </#if>
            <b>First Name: </b>${hit.firstName}<br>
            <b>Last Name: </b>${hit.lastName}<br>
            <b>DOB: </b>${hit.dob}<br>
            <b>Gender: </b>${hit.gender}<br>
            <b>Documents:</b><br>
            <div style="margin-left: 30px;">
                <table border="1">
                    <thead>
                        <tr>
                            <th>Document Type</th>
                            <th>Document Number</th>
                        </tr>
                    </thead>
                    <tbody>
                    <#list hit.documentDTOs as document>
                        <tr>
                            <td>${document.documentType}</td>
                            <td>${document.documentNumber}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
            <br>
            <b>Flight Number: </b>${hit.flightNumber}<br>
            <b>Flight Origin: </b>${hit.flightOrigin}<br>
            <b>Flight Destination: </b>${hit.flightDestination}<br>
            <b>Carrier: </b>${hit.carrier}<br>
            <b>Time Remaining before Departure: </b>${hit.timeRemaining}<br>
            <b>Hit Details: </b><br>
            <div style="margin-left: 30px;">
                <table border="1">
                    <tbody>
                        <tr>
                            <th>Severity</th>
                            <th>Category</th>
                            <th>Rule</th>
                            <th>Type</th>
                            <th>Status</th>
                        </tr>
                        <#list hit.categoryDTOs as category>
                            <tr>
                                <td>${category.severity}</td>
                                <td>${category.categoryName}</td>
                                <td>${category.rule}</td>
                                <td>${category.type}</td>
                                <td>${category.status}</td>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
            <br>
            <#if hit.hitEmailSenderDTO??>
                <p>Sent By: <font color="red">${hit.hitEmailSenderDTO.firstName} ${hit.hitEmailSenderDTO.lastName}</font> (${hit.hitEmailSenderDTO.userId})</p>
                <p><a href="${hit.urlToLoginPage}">GTAS Login</a></p>
            </#if>
            <hr size="2">
        </div>
    </#list>
</body>