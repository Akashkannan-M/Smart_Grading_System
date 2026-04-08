$body = '{"username":"HOD001","password":"01011980@hod"}'
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -TimeoutSec 15
    Write-Host "STATUS: $($response.StatusCode)"
    Write-Host "BODY: $($response.Content)"
} catch {
    Write-Host "ERROR STATUS: $($_.Exception.Response.StatusCode.value__)"
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    Write-Host "ERROR BODY: $($reader.ReadToEnd())"
}
