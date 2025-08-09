// QR Code Generation
document.getElementById('qrForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const dataTypes = formData.getAll('dataTypes');
    
    if (dataTypes.length === 0) {
        alert('Please select at least one data type to share');
        return;
    }
    
    const request = {
        dataTypes: dataTypes,
        expirationMinutes: parseInt(formData.get('expirationMinutes')),
        purpose: formData.get('purpose')
    };
    
    try {
        const response = await fetch('/api/qr/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getAuthToken()
            },
            body: JSON.stringify(request)
        });
        
        if (response.ok) {
            const result = await response.json();
            displayQRCode(result);
        } else {
            const error = await response.json();
            alert('Error: ' + error.message);
        }
    } catch (error) {
        alert('Network error: ' + error.message);
    }
});

function displayQRCode(result) {
    const qrResult = document.getElementById('qrResult');
    const qrImage = document.getElementById('qrImage');
    const expirationTime = document.getElementById('expirationTime');
    
    qrImage.src = 'data:image/png;base64,' + btoa(String.fromCharCode(...new Uint8Array(result.qrCodeImage)));
    expirationTime.textContent = new Date(result.expiresAt).toLocaleString();
    
    qrResult.style.display = 'block';
    
    // Set up cancel button
    document.getElementById('cancelSession').onclick = () => cancelSession(result.sessionId);
}

async function cancelSession(sessionId) {
    try {
        await fetch(`/api/qr/session/${sessionId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + getAuthToken()
            }
        });
        
        document.getElementById('qrResult').style.display = 'none';
        alert('QR session cancelled');
    } catch (error) {
        alert('Error cancelling session: ' + error.message);
    }
}

// QR Code Scanning (for providers)
let qrScanner;

if (document.getElementById('startScan')) {
    document.getElementById('startScan').addEventListener('click', startQRScan);
}

async function startQRScan() {
    const video = document.getElementById('qrVideo');
    
    try {
        qrScanner = new QrScanner(video, result => {
            processQRScan(result.data);
        });
        
        await qrScanner.start();
        document.getElementById('startScan').textContent = 'Stop Camera';
        document.getElementById('startScan').onclick = stopQRScan;
    } catch (error) {
        alert('Camera error: ' + error.message);
    }
}

function stopQRScan() {
    if (qrScanner) {
        qrScanner.stop();
        qrScanner = null;
    }
    document.getElementById('startScan').textContent = 'Start Camera';
    document.getElementById('startScan').onclick = startQRScan;
}

async function processQRScan(qrData) {
    try {
        const response = await fetch('/api/qr/scan', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getAuthToken()
            },
            body: JSON.stringify({ qrData: qrData })
        });
        
        if (response.ok) {
            const healthData = await response.json();
            displayHealthData(healthData);
            stopQRScan();
        } else {
            const error = await response.json();
            alert('Scan error: ' + error.message);
        }
    } catch (error) {
        alert('Network error: ' + error.message);
    }
}

function displayHealthData(bundle) {
    const display = document.getElementById('healthDataDisplay');
    const scanResult = document.getElementById('scanResult');
    
    let html = '<div class="accordion" id="healthDataAccordion">';
    
    bundle.entry.forEach((entry, index) => {
        const resource = entry.resource;
        const resourceType = resource.resourceType;
        
        html += `
            <div class="accordion-item">
                <h2 class="accordion-header" id="heading${index}">
                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" 
                            data-bs-target="#collapse${index}">
                        ${resourceType} - ${getResourceTitle(resource)}
                    </button>
                </h2>
                <div id="collapse${index}" class="accordion-collapse collapse" data-bs-parent="#healthDataAccordion">
                    <div class="accordion-body">
                        <pre>${JSON.stringify(resource, null, 2)}</pre>
                    </div>
                </div>
            </div>
        `;
    });
    
    html += '</div>';
    display.innerHTML = html;
    scanResult.style.display = 'block';
}

function getResourceTitle(resource) {
    switch (resource.resourceType) {
        case 'Patient':
            return `${resource.name?.[0]?.given?.[0] || ''} ${resource.name?.[0]?.family || ''}`;
        case 'Observation':
            return resource.code?.text || resource.code?.coding?.[0]?.display || 'Observation';
        case 'Condition':
            return resource.code?.text || resource.code?.coding?.[0]?.display || 'Condition';
        case 'MedicationRequest':
            return resource.medicationCodeableConcept?.text || 'Medication';
        default:
            return resource.resourceType;
    }
}

function getAuthToken() {
    // Implementation depends on your auth system
    // This could be from localStorage, sessionStorage, or a cookie
    return localStorage.getItem('authToken') || sessionStorage.getItem('authToken');
}