/**
* @desc Common functions used in the project, like confirmDelete...
* @author lxie
* 
*/
function confirmDelete()
{
	var x = confirm("Are you sure you want to delete?");
	if (x)
		return true;
	else
		return false;
}

function confirmAuthenticatPHR()
{
	var x = confirm("Have you verified the client with a photo id?");
	if (x)
		return true;
	else
		return false;
}