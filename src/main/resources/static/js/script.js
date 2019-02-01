function getCount(name){
	$.ajax({
		url: "/person",
		type: 'POST',
		contentType: 'application/json',
		dataType: 'json',
		data: JSON.stringify({
			"Name": name
		}),
		success: function (data) {
			console.log(data);
			document.getElementById("output").innerHTML = data.Count;
		},
		error: function(xhr, status, error){
			alert("#ERR: xhr.status=" + xhr.status + ", xhr.statusText=" + xhr.statusText + "\nstatus=" + status + ", error=" + error);
		}
    });
}