<head>
    <title>Generated Hits</title>
</head>
<body>
    <table border="1" align="center">
        <thead>
            <tr>
                <th><strong>First Name</strong></th>
                <th><strong>Last Name</strong></th>
                <th><strong>Flight Number</strong></th>
                <th><strong>DOB</strong></th>
                <th><strong>Gender</strong></th>
                <th><strong>Document Type | Document Number</strong></th>
                <th><strong>Severity | Category | Rule (Type)</strong></th>
                <th><strong>Time Remaining</strong></th>
            </tr>
        </thead>
        <tbody>
            <#list hits as hit>
                <tr>
                    <td align="center">${hit.firstName}</td>
                    <td align="center">${hit.lastName}</td>
                    <td align="center">${hit.flightNumber}</td>
                    <td align="center">${hit.dob}</td>
                    <td align="center">${hit.gender}</td>
                    <td align="center">
                        <ul>
                            <#list hit.documentDTOs as document>
                                 <li align="center">${document.documentType} | ${document.documentNumber}</li>
                            </#list>
                        </ul>
                    </td>
                    <td align="center">
                        <ul>
                            <#list hit.categoryDTOs as category>
                                <li align="center">${category.description}</li>
                            </#list>
                        </ul>
                    </td>
                    <td align="center">${hit.timeRemaining}</td>
                </tr>
            </#list>
        </tbody>
    </table>
</body>