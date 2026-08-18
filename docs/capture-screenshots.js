// One-off script to drive the running app with a headless browser and capture
// real screenshots of every major screen for the assignment report/documentation.
// Usage: node docs/capture-screenshots.js   (app must already be running on :8090)
const { chromium } = require('playwright');
const path = require('path');

const BASE = 'http://localhost:8090';
const OUT = path.join(__dirname, 'screenshots');

(async () => {
    const browser = await chromium.launch();
    const context = await browser.newContext({ viewport: { width: 1360, height: 900 } });
    const page = await context.newPage();

    const shot = async (name) => {
        await page.screenshot({ path: path.join(OUT, name), fullPage: true });
        console.log('captured', name);
    };

    // 1. Login page
    await page.goto(`${BASE}/login`);
    await shot('01-login.png');

    // 1b. Failed login (validation / auth feedback)
    await page.fill('#username', 'admin');
    await page.fill('#password', 'WrongPassword');
    await page.click('button[type=submit]');
    await page.waitForLoadState('networkidle');
    await shot('02-login-error.png');

    // 2. Successful login as admin -> dashboard (main menu)
    await page.fill('#username', 'admin');
    await page.fill('#password', 'Admin@123');
    await page.click('button[type=submit]');
    await page.waitForLoadState('networkidle');
    await shot('03-dashboard.png');

    // 3. Register appointment form (blank)
    await page.goto(`${BASE}/appointments/new`);
    await shot('04-appointment-form-blank.png');

    // 3b. Submit with validation errors
    await page.fill('#contactNumber', '123');
    await page.click('#btnSaveAppointment');
    await page.waitForLoadState('networkidle');
    await shot('05-appointment-form-validation-errors.png');

    // 3c. Fill in correctly and submit
    await page.goto(`${BASE}/appointments/new`);
    await page.fill('#patientName', 'Nadeesha Perera');
    await page.fill('#address', 'No. 45, Nugegoda, Colombo');
    await page.fill('#contactNumber', '0719876543');
    await page.selectOption('#dentistId', { index: 1 });
    await page.selectOption('#treatmentTypeId', { index: 4 }); // Root Canal
    const futureDate = new Date(Date.now() + 3 * 86400000).toISOString().slice(0, 10);
    await page.fill('#appointmentDate', futureDate);
    await page.fill('#appointmentTime', '10:30');
    await page.fill('#notes', 'Patient reports sensitivity on upper left molar.');
    await page.click('#btnSaveAppointment');
    await page.waitForLoadState('networkidle');
    await shot('06-appointment-registered-detail.png');

    const url = page.url();
    const appointmentNumber = url.split('/').pop();
    console.log('Registered appointment:', appointmentNumber);

    // 4. Search screen
    await page.goto(`${BASE}/appointments/search`);
    await shot('07-appointment-search.png');

    await page.fill('#appointmentNumber', appointmentNumber);
    await page.click('#btnSearchAppointment');
    await page.waitForLoadState('networkidle');
    await shot('08-appointment-found.png');

    // 4b. Search - not found case
    await page.goto(`${BASE}/appointments/search`);
    await page.fill('#appointmentNumber', 'APT-999999');
    await page.click('#btnSearchAppointment');
    await page.waitForLoadState('networkidle');
    await shot('09-appointment-not-found.png');

    // 5. Generate & view bill
    await page.goto(`${BASE}/appointments/${appointmentNumber}`);
    await page.click('button:has-text("Calculate")');
    await page.waitForLoadState('networkidle');
    await shot('10-bill-receipt.png');

    // 6. Reports (admin)
    await page.goto(`${BASE}/reports`);
    await shot('11-reports-form.png');

    const today = new Date().toISOString().slice(0, 10);
    const monthStart = today.slice(0, 8) + '01';
    await page.goto(`${BASE}/reports?type=DAILY_APPOINTMENTS&from=${monthStart}&to=${futureDate}`);
    await page.waitForLoadState('networkidle');
    await shot('12-report-daily-appointments.png');

    await page.goto(`${BASE}/reports?type=REVENUE&from=${monthStart}&to=${futureDate}`);
    await page.waitForLoadState('networkidle');
    await shot('13-report-revenue.png');

    await page.goto(`${BASE}/reports?type=DENTIST_WORKLOAD&from=${monthStart}&to=${futureDate}`);
    await page.waitForLoadState('networkidle');
    await shot('14-report-dentist-workload.png');

    // 7. Help page
    await page.goto(`${BASE}/help`);
    await shot('15-help.png');

    // 8. Logout (must be a POST per CSRF protection, so click the real button)
    await page.goto(`${BASE}/dashboard`);
    await page.click('#btnLogout');
    await page.waitForLoadState('networkidle');
    await shot('16-logged-out.png');

    // 9. Role-based access: receptionist tries Reports -> 403
    await page.fill('#username', 'reception');
    await page.fill('#password', 'Reception@123');
    await page.click('button[type=submit]');
    await page.waitForLoadState('networkidle');
    await page.goto(`${BASE}/reports`);
    await shot('17-receptionist-forbidden-from-reports.png');

    // 10. Double-booking prevention demo
    await page.goto(`${BASE}/appointments/new`);
    await page.fill('#patientName', 'Second Patient Same Slot');
    await page.fill('#address', 'Colombo 07');
    await page.fill('#contactNumber', '0711112222');
    await page.selectOption('#dentistId', { index: 1 });
    await page.selectOption('#treatmentTypeId', { index: 1 });
    await page.fill('#appointmentDate', futureDate);
    await page.fill('#appointmentTime', '10:30'); // same as earlier booking, same dentist
    await page.click('#btnSaveAppointment');
    await page.waitForLoadState('networkidle');
    await shot('18-double-booking-prevented.png');

    // 11. REST web service responding with JSON (Task B.i evidence) - Chromium
    // renders application/json responses with its built-in JSON viewer.
    await page.goto(`http://admin:Admin%40123@localhost:8090/api/appointments/${appointmentNumber}`);
    await page.click('input[type=checkbox]').catch(() => {});
    await shot('19-rest-api-json-response.png');

    await page.goto(`http://admin:Admin%40123@localhost:8090/api/appointments/${appointmentNumber}/bill`);
    await page.click('input[type=checkbox]').catch(() => {});
    await shot('20-rest-api-bill-json.png');

    await browser.close();
    console.log('All screenshots captured to', OUT);
})();
