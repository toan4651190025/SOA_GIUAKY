const endpoints = { sinhvien: "/api/sinhvien", detai: "/api/detai", dangky: "/api/dangky" };
const config = {
  sinhvien: {
    title: "Sinh viên", subtitle: "Danh sách sinh viên thực hiện đồ án tốt nghiệp", list: "Hồ sơ sinh viên", add: "Thêm sinh viên",
    columns: ["MÃ SINH VIÊN", "SINH VIÊN", "NGÀNH ĐÀO TẠO", "THAO TÁC"],
    fields: [
      ["maSinhVien", "Mã sinh viên", "text", true], ["hoTen", "Họ và tên", "text", true],
      ["email", "Email", "email", true], ["nganh", "Ngành", "text", true]
    ]
  },
  detai: {
    title: "Đề tài", subtitle: "Quản lý danh mục đề tài và chỉ tiêu hướng dẫn", list: "Danh mục đề tài", add: "Thêm đề tài",
    columns: ["MÃ ĐỀ TÀI", "TÊN ĐỀ TÀI", "GIẢNG VIÊN HƯỚNG DẪN", "CHỈ TIÊU", "THAO TÁC"],
    fields: [
      ["maDeTai", "Mã đề tài", "text", true], ["tenDeTai", "Tên đề tài", "text", true],
      ["giangVienHuongDan", "Giảng viên hướng dẫn", "text", true], ["chiTieu", "Chỉ tiêu", "number", true, "min=1"],
      ["moTa", "Mô tả", "textarea", false]
    ]
  },
  dangky: {
    title: "Đăng ký & điểm", subtitle: "Theo dõi phân công, tiến độ và kết quả đồ án", list: "Danh sách đăng ký", add: "Thêm đăng ký",
    columns: ["SINH VIÊN", "ĐỀ TÀI", "TRẠNG THÁI", "ĐIỂM HƯỚNG DẪN", "ĐIỂM PHẢN BIỆN", "ĐIỂM HỘI ĐỒNG", "TỔNG KẾT", "THAO TÁC"],
    fields: []
  }
};

const state = { active: "sinhvien", rows: [], students: [], topics: [], editId: null, toastTimer: null };
const element = (id) => document.getElementById(id);

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (char) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char]);
}

function displayStatus(value) {
  return ({ DANG_KY: "Đăng ký", DANG_THUC_HIEN: "Đang thực hiện", DA_HOAN_THANH: "Đã hoàn thành" })[value] || value || "Đăng ký";
}

async function request(url, options = {}) {
  const response = await fetch(url, { ...options, headers: { "Content-Type": "application/json", ...options.headers } });
  if (!response.ok) {
    let message = `Yêu cầu thất bại (${response.status})`;
    try {
      const body = await response.json();
      message = body.detail || body.message || message;
    } catch { /* Keep the HTTP status message. */ }
    throw new Error(message);
  }
  return response.status === 204 ? null : response.json();
}

async function loadData() {
  try {
    const [students, topics, registrations] = await Promise.all([
      request(endpoints.sinhvien), request(endpoints.detai), request(endpoints.dangky)
    ]);
    state.students = students;
    state.topics = topics;
    state.rows = { sinhvien: students, detai: topics, dangky: registrations }[state.active];
    updateMetrics(students, topics, registrations);
    renderTable();
    element("last-updated").textContent = `Cập nhật lúc ${new Date().toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" })}`;
  } catch (error) {
    element("table-body").innerHTML = `<tr><td class="empty-state" colspan="8">${escapeHtml(error.message)} · Kiểm tra dịch vụ rồi tải lại.</td></tr>`;
    showToast(error.message);
  }
}

function updateMetrics(students, topics, registrations) {
  element("metric-students").textContent = students.length;
  element("metric-topics").textContent = topics.length;
  element("metric-registrations").textContent = registrations.length;
  element("nav-students").textContent = students.length;
  element("nav-topics").textContent = topics.length;
  element("nav-registrations").textContent = registrations.length;
  const scored = registrations.filter((item) => item.diemTongKet !== null);
  const average = scored.length ? (scored.reduce((sum, item) => sum + item.diemTongKet, 0) / scored.length).toFixed(2) : null;
  element("metric-score-note").textContent = average ? `Điểm trung bình ${average} / 10` : "Chưa có dữ liệu điểm";
}

function renderTable() {
  const view = config[state.active];
  element("table-head").innerHTML = `<tr>${view.columns.map((label) => `<th>${label}</th>`).join("")}</tr>`;
  const search = element("search-input").value.trim().toLocaleLowerCase("vi");
  const status = element("status-filter").value;
  const rows = state.rows.filter((row) => {
    const matchesText = JSON.stringify(row).toLocaleLowerCase("vi").includes(search);
    return matchesText && (state.active !== "dangky" || !status || row.trangThai === status);
  });
  element("result-count").textContent = `${rows.length} bản ghi`;
  element("status-filter").hidden = state.active !== "dangky";
  if (!rows.length) {
    element("table-body").innerHTML = `<tr><td class="empty-state" colspan="${view.columns.length}">Không có dữ liệu phù hợp.</td></tr>`;
    return;
  }
  element("table-body").innerHTML = rows.map((row) => `<tr>${rowCells(row)}<td>${actionButtons(row.id)}</td></tr>`).join("");
  element("table-body").querySelectorAll("[data-edit]").forEach((button) => button.addEventListener("click", () => openDialog(Number(button.dataset.edit))));
  element("table-body").querySelectorAll("[data-delete]").forEach((button) => button.addEventListener("click", () => deleteRow(Number(button.dataset.delete))));
}

function rowCells(row) {
  if (state.active === "sinhvien") return `<td><span class="code">${escapeHtml(row.maSinhVien)}</span></td><td><span class="cell-primary">${escapeHtml(row.hoTen)}</span><span class="cell-secondary">${escapeHtml(row.email)}</span></td><td>${escapeHtml(row.nganh)}</td>`;
  if (state.active === "detai") return `<td><span class="code">${escapeHtml(row.maDeTai)}</span></td><td><span class="cell-primary">${escapeHtml(row.tenDeTai)}</span><span class="cell-secondary">${escapeHtml(row.moTa || "Chưa có mô tả")}</span></td><td>${escapeHtml(row.giangVienHuongDan)}</td><td>${row.chiTieu}</td>`;
  const statusClass = row.trangThai === "DA_HOAN_THANH" ? "done" : row.trangThai === "DANG_KY" ? "pending" : "";
  const score = (value) => value === null ? `<span class="score-muted">—</span>` : escapeHtml(Number(value).toFixed(1));
  const total = row.diemTongKet === null ? `<span class="score-muted">Chưa chấm</span>` : `<span class="score">${escapeHtml(Number(row.diemTongKet).toFixed(2))}</span><span class="cell-secondary">${row.ketQua === "DAT" ? "ĐẠT" : "CHƯA ĐẠT"}</span>`;
  return `<td><span class="cell-primary">${escapeHtml(row.tenSinhVien)}</span><span class="cell-secondary">${escapeHtml(row.maSinhVien)}</span></td><td><span class="cell-primary">${escapeHtml(row.tenDeTai)}</span></td><td><span class="status-pill ${statusClass}">${escapeHtml(displayStatus(row.trangThai))}</span></td><td>${score(row.diemHuongDan)}</td><td>${score(row.diemPhanBien)}</td><td>${score(row.diemHoiDong)}</td><td>${total}</td>`;
}

function actionButtons(id) {
  return `<div class="row-actions"><button class="text-action" type="button" data-edit="${id}">Sửa</button><button class="text-action danger" type="button" data-delete="${id}">Xóa</button></div>`;
}

function setView(view) {
  state.active = view;
  document.querySelectorAll(".nav-item").forEach((button) => button.classList.toggle("active", button.dataset.view === view));
  const viewConfig = config[view];
  element("page-title").textContent = viewConfig.title;
  element("page-subtitle").textContent = viewConfig.subtitle;
  element("list-title").textContent = viewConfig.list;
  element("add-label").textContent = viewConfig.add;
  element("search-input").value = "";
  loadData();
}

function fieldMarkup([name, label, type, required, extra = ""], value = "") {
  const full = type === "textarea" || name === "tenDeTai" ? " full" : "";
  const safeValue = escapeHtml(value);
  let control;
  if (type === "textarea") control = `<textarea id="field-${name}" name="${name}" ${required ? "required" : ""}>${safeValue}</textarea>`;
  else control = `<input id="field-${name}" name="${name}" type="${type}" value="${safeValue}" ${required ? "required" : ""} ${extra}>`;
  return `<div class="field${full}"><label for="field-${name}">${label}${required ? " *" : ""}</label>${control}</div>`;
}

function registrationFields(row = {}) {
  const studentOptions = state.students.map((student) => `<option value="${student.id}" ${row.sinhVienId === student.id ? "selected" : ""}>${escapeHtml(student.maSinhVien)} · ${escapeHtml(student.hoTen)}</option>`).join("");
  const topicOptions = state.topics.map((topic) => `<option value="${topic.id}" ${row.deTaiId === topic.id ? "selected" : ""}>${escapeHtml(topic.maDeTai)} · ${escapeHtml(topic.tenDeTai)}</option>`).join("");
  const statuses = ["DANG_KY", "DANG_THUC_HIEN", "DA_HOAN_THANH"];
  const statusOptions = statuses.map((status) => `<option value="${status}" ${(row.trangThai || "DANG_KY") === status ? "selected" : ""}>${displayStatus(status)}</option>`).join("");
  const select = (name, label, options) => `<div class="field full"><label for="field-${name}">${label} *</label><select id="field-${name}" name="${name}" required>${options}</select></div>`;
  const scores = [["diemHuongDan", "Điểm hướng dẫn"], ["diemPhanBien", "Điểm phản biện"], ["diemHoiDong", "Điểm hội đồng"]];
  return select("sinhVienId", "Sinh viên", `<option value="">Chọn sinh viên</option>${studentOptions}`)
    + select("deTaiId", "Đề tài", `<option value="">Chọn đề tài</option>${topicOptions}`)
    + select("trangThai", "Trạng thái", statusOptions)
    + scores.map(([name, label]) => fieldMarkup([name, label, "number", false, "min=0 max=10 step=0.1"], row[name] ?? "")).join("");
}

function openDialog(id = null) {
  state.editId = id;
  const row = id === null ? {} : state.rows.find((item) => item.id === id);
  element("dialog-title").textContent = id === null ? config[state.active].add : "Chỉnh sửa hồ sơ";
  element("form-fields").innerHTML = state.active === "dangky"
    ? registrationFields(row)
    : config[state.active].fields.map((field) => fieldMarkup(field, row?.[field[0]] ?? "")).join("");
  element("form-error").textContent = "";
  element("modal-backdrop").hidden = false;
  document.querySelector("#form-fields input, #form-fields select")?.focus();
}

function closeDialog() {
  element("modal-backdrop").hidden = true;
  element("record-form").reset();
}

async function saveRecord(event) {
  event.preventDefault();
  const formData = new FormData(event.currentTarget);
  const payload = Object.fromEntries(formData.entries());
  if (state.active === "detai") payload.chiTieu = Number(payload.chiTieu);
  if (state.active === "dangky") {
    payload.sinhVienId = Number(payload.sinhVienId);
    payload.deTaiId = Number(payload.deTaiId);
    for (const key of ["diemHuongDan", "diemPhanBien", "diemHoiDong"]) payload[key] = payload[key] === "" ? null : Number(payload[key]);
  }
  const url = state.editId === null ? endpoints[state.active] : `${endpoints[state.active]}/${state.editId}`;
  const method = state.editId === null ? "POST" : "PUT";
  element("save-button").disabled = true;
  try {
    await request(url, { method, body: JSON.stringify(payload) });
    closeDialog();
    showToast(state.editId === null ? "Đã tạo hồ sơ." : "Đã cập nhật hồ sơ.");
    await loadData();
  } catch (error) {
    element("form-error").textContent = error.message;
  } finally {
    element("save-button").disabled = false;
  }
}

async function deleteRow(id) {
  if (!window.confirm("Xóa bản ghi này? Thao tác không thể hoàn tác.")) return;
  try {
    await request(`${endpoints[state.active]}/${id}`, { method: "DELETE" });
    showToast("Đã xóa hồ sơ.");
    await loadData();
  } catch (error) { showToast(error.message); }
}

function showToast(message) {
  const toast = element("toast");
  toast.textContent = message;
  toast.classList.add("visible");
  clearTimeout(state.toastTimer);
  state.toastTimer = setTimeout(() => toast.classList.remove("visible"), 3200);
}

document.querySelectorAll(".nav-item").forEach((button) => button.addEventListener("click", () => setView(button.dataset.view)));
element("add-button").addEventListener("click", () => openDialog());
element("refresh-button").addEventListener("click", loadData);
element("search-input").addEventListener("input", renderTable);
element("status-filter").addEventListener("change", renderTable);
element("record-form").addEventListener("submit", saveRecord);
element("close-dialog").addEventListener("click", closeDialog);
element("cancel-dialog").addEventListener("click", closeDialog);
element("modal-backdrop").addEventListener("click", (event) => { if (event.target === element("modal-backdrop")) closeDialog(); });
document.addEventListener("keydown", (event) => { if (event.key === "Escape" && !element("modal-backdrop").hidden) closeDialog(); });
element("today-label").textContent = new Intl.DateTimeFormat("vi-VN", { weekday: "long", day: "2-digit", month: "long", year: "numeric" }).format(new Date());
loadData();