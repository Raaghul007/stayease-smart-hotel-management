// Change these paths if your Spring Boot controller mappings are different.
const DEFAULT_API = "http://localhost:8080";
let API = localStorage.getItem("hotelApiBase") || DEFAULT_API;
const endpoints = {
  rooms: "/api/rooms",
  customers: "/api/customers",
  bookings: "/api/bookings",
  payments: "/api/payments",
  staff: "/api/staff"
};

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => [...document.querySelectorAll(selector)];

function toast(message) {
  const el = $("#toast");
  el.textContent = message;
  el.classList.add("show");
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => el.classList.remove("show"), 3200);
}
function getApiUrl(path) { return API.replace(/\/$/, "") + path; }
async function api(path, options = {}) {
  const response = await fetch(getApiUrl(path), {
    ...options,
    headers: { "Content-Type": "application/json", ...(options.headers || {}) }
  });
  const raw = await response.text();
  let data = null;
  try { data = raw ? JSON.parse(raw) : null; } catch { data = raw; }
  if (!response.ok) {
    const message = data?.message || data?.error || raw || `HTTP ${response.status}`;
    throw new Error(message);
  }
  return data;
}
function rowsFrom(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(data?.data)) return data.data;
  return data ? [data] : [];
}
function safe(value) {
  return String(value ?? "—").replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
}
function table(container, columns, data) {
  const items = rowsFrom(data);
  if (!items.length) { container.innerHTML = '<div class="empty">No records found.</div>'; return; }
  container.innerHTML = `<table><thead><tr>${columns.map(c=>`<th>${c.label}</th>`).join("")}</tr></thead><tbody>${
    items.map(item=>`<tr>${columns.map(c=>`<td>${c.render ? c.render(item) : safe(item[c.key])}</td>`).join("")}</tr>`).join("")
  }</tbody></table>`;
}
async function loadList(kind, target, columns) {
  try {
    const data = await api(endpoints[kind]);
    table($(target), columns, data);
    return rowsFrom(data);
  } catch (e) {
    $(target).innerHTML = `<div class="empty">Could not load ${kind}. ${safe(e.message)}<br>Check the API URL, endpoint and backend CORS settings.</div>`;
    return [];
  }
}
const cols = {
  rooms: [
    {label:"ID",key:"id"},{label:"Room no.",key:"roomNumber"},
    {label:"Type",key:"roomType"},{label:"Price / night",render:r=>"₹"+safe(r.pricePerNight)},
    {label:"Availability",render:r=>`<span class="pill">${safe(r.availabilityStatus || r.status)}</span>`}
  ],
  customers: [
    {label:"ID",key:"id"},{label:"Customer",key:"customerName"},
    {label:"Email",key:"email"},{label:"Phone",key:"phoneNumber"},{label:"Address",key:"address"}
  ],
  bookings: [
    {label:"Booking ID",key:"id"},
    {label:"Customer",render:b=>safe(b.customer?.customerName || b.customerName || b.customerId)},
    {label:"Room",render:b=>safe(b.room?.roomNumber || b.roomNumber || b.roomId)},
    {label:"Check-in",key:"checkInDate"},{label:"Check-out",key:"checkOutDate"},
    {label:"Total",render:b=>"₹"+safe(b.totalAmount)},{label:"Status",render:b=>`<span class="pill">${safe(b.bookingStatus || b.status)}</span>`}
  ],
  payments: [
    {label:"Payment ID",key:"id"},
    {label:"Booking ID",render:p=>safe(p.booking?.id || p.bookingId)},
    {label:"Amount",render:p=>"₹"+safe(p.amount)},
    {label:"Method",key:"paymentMethod"},
    {label:"Status",render:p=>`<span class="pill">${safe(p.paymentStatus)}</span>`},
    {label:"Date",key:"paymentDate"}
  ],
  staff: [
    {label:"ID",key:"id"},{label:"Staff name",key:"staffName"},
    {label:"Email",key:"email"},{label:"Phone",key:"phone"},
    {label:"Designation",key:"designation"},
    {label:"Salary",render:s=>"₹"+safe(s.salary)},{label:"Role",key:"role"}
  ]
};
async function refreshDashboard() {
  const kinds = ["rooms","customers","bookings","payments"];
  const results = await Promise.all(kinds.map(k => api(endpoints[k]).then(rowsFrom).catch(()=>null)));
  ["room-count","customer-count","booking-count","payment-count"].forEach((id,i)=>{
    $( "#" + id).textContent = results[i] ? results[i].length : "—";
  });
  if (results[2]) {
    const recent = results[2].slice(-5).reverse();
    table($("#recent-bookings"), [
      {label:"Booking",render:b=>"#"+safe(b.id)},
      {label:"Customer",render:b=>safe(b.customer?.customerName || b.customerName || b.customerId)},
      {label:"Room",render:b=>safe(b.room?.roomNumber || b.roomNumber || b.roomId)},
      {label:"Dates",render:b=>`${safe(b.checkInDate)} → ${safe(b.checkOutDate)}`},
      {label:"Status",render:b=>`<span class="pill">${safe(b.bookingStatus || b.status)}</span>`}
    ], recent);
  } else $("#recent-bookings").innerHTML = '<div class="empty">Dashboard data will appear when the backend APIs are available.</div>';
}
async function refreshPage(page) {
  if (page === "dashboard") return refreshDashboard();
  if (page === "rooms") return loadList("rooms","#rooms-table",cols.rooms);
  if (page === "customers") return loadList("customers","#customers-table",cols.customers);
  if (page === "bookings") return loadList("bookings","#bookings-table",cols.bookings);
  if (page === "payments") return loadList("payments","#payments-table",cols.payments);
  if (page === "staff") return loadList("staff","#staff-table",cols.staff);
}
function showPage(page) {
  $$(".page").forEach(p=>p.classList.toggle("active",p.id===page));
  $$(".nav-item").forEach(b=>b.classList.toggle("active",b.dataset.page===page));
  const title = page.charAt(0).toUpperCase()+page.slice(1);
  $("#page-title").textContent = title;
  refreshPage(page);
}
$$(".nav-item").forEach(btn=>btn.addEventListener("click",()=>showPage(btn.dataset.page)));
$$("[data-go]").forEach(btn=>btn.addEventListener("click",()=>showPage(btn.dataset.go)));

function formData(form) { return Object.fromEntries(new FormData(form).entries()); }
function submitForm(id, path, transform, success, after) {
  $(id).addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;
    const button = form.querySelector('button[type="submit"]');
    button.disabled = true; button.textContent = "Saving…";
    try {
      const body = transform ? transform(formData(form)) : formData(form);
      await api(path,{method:"POST",body:JSON.stringify(body)});
      toast(success);
      form.reset();
      if (after) await after();
    } catch (e) { toast("Error: " + e.message); }
    finally { button.disabled = false; button.textContent = button.dataset.label || "Save"; }
  });
}
$("#room-form").querySelector("button").dataset.label = "Save room";
submitForm("#room-form",endpoints.rooms,d=>({...d,pricePerNight:Number(d.pricePerNight)}),"Room saved successfully.",()=>refreshPage("rooms"));
$("#customer-form").querySelector("button").dataset.label = "Save customer";
submitForm(
    "#customer-form",
    endpoints.customers,
    d => ({
        customerName: d.customerName,
        email: d.email,
        phoneNumber: d.phoneNumber || d.phone,
        address: d.address
    }),
    "Customer registered successfully.",
    () => refreshPage("customers")
);

$("#booking-form").querySelector("button").dataset.label = "Create booking";

submitForm(
    "#booking-form",
    endpoints.bookings,
    d => ({
        customerId: Number(d.customerId),
        roomId: Number(d.roomId),
        checkInDate: d.checkInDate,
        checkOutDate: d.checkOutDate
    }),
    "Booking created successfully.",
    () => refreshPage("bookings")
);

$("#payment-form").querySelector("button").dataset.label = "Submit payment";

submitForm(
    "#payment-form",
    endpoints.payments,
    d => ({
        bookingId: Number(d.bookingId),
        amount: Number(d.amount),
        paymentMethod: d.paymentMethod,
        paymentStatus: d.paymentStatus
    }),
    "Payment recorded successfully.",
    () => refreshPage("payments")
);


$("#staff-form").querySelector("button").dataset.label = "Register staff";

submitForm(
    "#staff-form",
    endpoints.staff,
    d => ({
        staffName: d.staffName,
        email: d.email,
        phone: d.phone,
        designation: d.designation,
        salary: Number(d.salary),
        role: d.role
    }),
    "Staff registered successfully.",
    () => refreshPage("staff")
);


[
  ["#refresh-rooms","rooms"],["#refresh-customers","customers"],
  ["#refresh-bookings","bookings"],["#refresh-payments","payments"],
  ["#refresh-staff","staff"]
].forEach(([button,page])=>$(button).addEventListener("click",()=>refreshPage(page)));

$("#save-api").addEventListener("click",()=>{
  API = $("#api-base").value.trim() || DEFAULT_API;
  localStorage.setItem("hotelApiBase",API);
  toast("Backend URL saved.");
  refreshPage("dashboard");
});
$("#api-base").value = API;
$("#today").textContent = new Date().toLocaleDateString("en-IN",{weekday:"long",day:"numeric",month:"long",year:"numeric"});
refreshPage("dashboard");
