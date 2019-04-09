<%@ page session="false" %>
<html>
<head>
<title>Upload File Request Page</title>
</head>
<body>
	<form method="POST" action="uploadFile" enctype="multipart/form-data">
	<table>
	<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
		<td>
			<table>
				<tr>
					<td>File to upload: <input type="file" name="file"></td>
				</tr>
				<tr>
					<td>Name: <input type="text" name="name"></td>
				</tr>
				<tr>
					<td><input type="submit" value="Upload"> Press here to upload the file!</td>
				</tr>
			</table>
		
		</td>
	</tr>
	</table>

	</form>	
</body>
</html>
