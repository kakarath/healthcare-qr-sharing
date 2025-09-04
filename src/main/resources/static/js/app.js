// QR Code Generation
if (document.getElementById('qrForm')) {
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
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(request)
            });
            
            if (response.ok) {
                const result = await response.json();
                console.log('QR Response:', result);
                displayQRCode(result);
            } else {
                const errorText = await response.text();
                console.error('QR Error:', errorText);
                alert('Error: ' + response.status + ' - ' + errorText);
            }
        } catch (error) {
            alert('Error: ' + error.message);
        }
    });
}

function displayQRCode(result) {
    const qrResult = document.getElementById('qrResult');
    const qrImage = document.getElementById('qrImage');
    const expirationTime = document.getElementById('expirationTime');
    const generateBtn = document.querySelector('button[type="submit"]');
    
    qrImage.src = 'data:image/png;base64,' + result.qrCodeImage;
    expirationTime.textContent = result.expiresAt || new Date(Date.now() + 15*60*1000).toLocaleString();
    
    qrResult.style.display = 'block';
    generateBtn.disabled = true;
    generateBtn.textContent = 'QR Code Generated';
    
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
        const generateBtn = document.querySelector('button[type="submit"]');
        generateBtn.disabled = false;
        generateBtn.textContent = 'Generate QR Code';
        alert('QR session cancelled');
    } catch (error) {
        alert('Error cancelling session: ' + error.message);
    }
}

// QR Code Scanning (for providers)
let qrScanner;
let isScanning = false;

if (document.getElementById('startScan')) {
    const button = document.getElementById('startScan');
    button.addEventListener('click', function() {
        if (isScanning) {
            stopQRScan();
        } else {
            startQRScan();
        }
    });
    
    // Clean up when page unloads
    window.addEventListener('beforeunload', () => {
        if (qrScanner) {
            qrScanner.destroy();
        }
    });
}

async function startQRScan() {
    const video = document.getElementById('qrVideo');
    const button = document.getElementById('startScan');
    
    try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } });
        video.srcObject = stream;
        
        qrScanner = new QrScanner(video, result => {
            processQRScan(result.data);
        }, {
            returnDetailedScanResult: true,
            highlightScanRegion: true
        });
        
        await qrScanner.start();
        isScanning = true;
        button.textContent = 'Stop Camera';
        button.classList.remove('btn-success');
        button.classList.add('btn-danger');
    } catch (error) {
        alert('Camera error: ' + error.message);
        isScanning = false;
    }
}

function stopQRScan() {
    const button = document.getElementById('startScan');
    const video = document.getElementById('qrVideo');
    
    if (qrScanner) {
        qrScanner.stop();
        qrScanner.destroy();
        qrScanner = null;
    }
    
    // Stop all video tracks
    if (video.srcObject) {
        video.srcObject.getTracks().forEach(track => {
            track.stop();
        });
        video.srcObject = null;
    }
    
    isScanning = false;
    button.textContent = 'Start Camera';
    button.classList.remove('btn-danger');
    button.classList.add('btn-success');
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