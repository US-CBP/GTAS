<?php
// Check for empty fields
if(empty($_POST['name'])      ||
   empty($_POST['email'])     ||
   empty($_POST['agency'])     ||
   empty($_POST['message'])   ||
   !filter_var($_POST['email'],FILTER_VALIDATE_EMAIL))
   {
   echo "No arguments Provided!";
   return false;
   }

$name = strip_tags(htmlspecialchars($_POST['name']));
$email_address = strip_tags(htmlspecialchars($_POST['email']));
$agency = strip_tags(htmlspecialchars($_POST['agency']));
$message = strip_tags(htmlspecialchars($_POST['message']));

// Create the email and send the message
$to = 'david.j.ertel@gmail.com'; // Add your email address in between the '' replacing yourname@yourdomain.com - This is where the form will send a message to.
$email_subject = "Website Contact Form:  $name";
$email_body = "You have received a new message from the GTATS GitHub IO site.\n\n"."Here are the details:\n\nName: $name\n\nEmail: $email_address\n\nAgency: $agency\n\nMessage:\n$message";
$headers = "From: noreply@wcoomd.org\n"; // This is the email address the generated message will be from. We recommend using something like noreply@yourdomain.com.
$headers .= "Reply-To: $email_address";
//mail($to,$email_subject,$email_body,$headers);

return true;
?>
