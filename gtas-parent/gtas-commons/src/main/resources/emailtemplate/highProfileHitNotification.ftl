<head>
    <title>Generated Hits</title>
</head>
<body>
    <p>This is a system generated email caused by elevated risk categories for the following <font color="red"><b>${hits?size}</b></font> passenger(s):</p>
    <#list hits as hit>
        <font color="red"><b>#${hit?index+1}</b></font>
        <div style="margin-left: 30px; border-bottom: black">
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
            <hr size="2">
        </div>
    </#list>
</body>