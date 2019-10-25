
    <h1>Generated Hits</h1>
    <table>
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
                <td>${hit.firstName}</td>
                <td>${hit.lastName}</td>
                <td>${hit.flightNumber}</td>
                <td>${hit.dob}</td>
                <td>${hit.gender}</td>
                <td>
                    <ul>
                        <#list hit.documentDTOs as document>
                            ${document.documentType} | ${document.documentNumber}
                        </#list>
                    </ul>
                </td>
                <td>
                    <ul>
                        <#list hit.categoryDTOs as category>
                           <li>${category.description}</li>
                        </#list>
                    </ul>
                </td>
                <td>${hit.timeRemaining}</td>
            </tr>
        </#list>
        </tbody>
    </table>