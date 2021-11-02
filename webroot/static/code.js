const serviceTable = document.querySelector('#service-table');
let servicesRequest = new Request('http://localhost:8080/services');

refresh();

function refresh() {
    fetch(servicesRequest)
        .then(function (response) {
            return response.json();
        })
        .then(function (serviceList) {
            serviceList.forEach(service => {
                console.log("Service: " + JSON.stringify(service));

                let tr = document.createElement("tr");
                let tdName = document.createElement("td");
                let tdUrl = document.createElement("td");
                let tdStatus = document.createElement("td");
                let tdCreated = document.createElement("td");
                let tdUpdated = document.createElement("td");
                tdName.innerText = service.name;
                tdUrl.innerText = service.url;
                tdStatus.innerText = service.status;
                tdCreated.innerText = service.created;
                tdUpdated.innerText = service.updated;
                tr.appendChild(tdName)
                tr.appendChild(tdUrl)
                tr.appendChild(tdStatus)
                tr.appendChild(tdCreated)
                tr.appendChild(tdUpdated)
                serviceTable.appendChild(tr);
            });
        });
}

const refreshButton = document.querySelector('#get-service');
refreshButton.onclick = evt => {
    console.log("refreshButton.onclick")
    location.reload();
};

const saveButton = document.querySelector('#insert-service');
saveButton.onclick = evt => {
    console.log("saveButton.onclick")
    let url = document.querySelector('#url-input').value;
    let name = document.querySelector('#name-input').value;

    fetch('http://localhost:8080/services/' + url + "/" + name, {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({url: url, name: name})
    }).then(processError);
};

const updateButton = document.querySelector('#update-service');
updateButton.onclick = evt => {
    console.log("updateButton.onclick")
    let url = document.querySelector('#url-input').value;
    let name = document.querySelector('#name-input').value;

    fetch('http://localhost:8080/services/' + url + "/" + name, {
        method: 'PATCH',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'text/html'
        },
        body: JSON.stringify({url: url, name: name})
    }).then(processError);
};

const deleteButton = document.querySelector('#delete-service');
deleteButton.onclick = evt => {
    console.log("deleteButton.onclick")
    let url = document.querySelector('#url-input').value;

    fetch('http://localhost:8080/services/' + url, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'text/html'
        }
    }).then(res => location.reload());
};

function processError(res) {
    if (res.ok) {
        location.reload()
    } else {
        console.log(res.status);
        console.log(res.statusText);
        res.text().then(x => {
            console.log(x);
            let urlWarning = document.querySelector('#warning-label');
            urlWarning.innerText = x;
        });
    }
}