
    <h1>Generated Hits</h1>
    <table>
        <thead>
            <tr>
                <th><strong>Hit Status</strong></th>
                <th><strong>First Name</strong></th>
                <th><strong>Last Name</strong></th>
                <th><strong>Flight Number</strong></th>
                <th><strong>DOB</strong></th>
                <th><strong>Gender</strong></th>
                <th><strong>Document Type</strong></th>
                <th><strong>Document Number</strong></th>
                <th><strong>Severity | Category | Rule (Type)</strong></th>
                <th><strong>Time Remaining</strong></th>
            </tr>
        </thead>
        <tbody>
        <#list hits as hit>
            <tr>
                <td>${hit.hitStatus}</strong></td>
                <td>${hit.firstName}</strong></td>
                <td>${hit.lastName}</strong></td>
                <td>${hit.flightNumber}</strong></td>
                <td>${hit.dob}</strong></td>
                <td>${hit.gender}</strong></td>
                <td>${hit.documentType}</strong></td>
                <td>${hit.documentNumber}</strong></td>
                <td>${hit.description}</strong></td>
                <td>${hit.timeRemaining}</strong></td>
            </tr>
        </#list>
        </tbody>
    </table>