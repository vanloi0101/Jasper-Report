import React, { useState } from 'react';
import axios from 'axios';
import './ReportDownloader.css';

// Định nghĩa kiểu dữ liệu cho props (nếu có)
interface ReportDownloaderProps {}

// Định nghĩa kiểu cho định dạng báo cáo
type ReportFormat = 'pdf' | 'xlsx';

const ReportDownloader: React.FC<ReportDownloaderProps> = () => {
  const [rollNumber, setRollNumber] = useState<number | ''>('');
  const [format, setFormat] = useState<ReportFormat>('pdf');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const handleDownload = async () => {
    if (!rollNumber) {
      setError('Vui lòng nhập mã số học sinh');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await axios.get(
        `http://localhost:8080/api/v1/reports/student/${rollNumber}?format=${format}`,
        { responseType: 'blob' } // Để xử lý file nhị phân (PDF/XLSX)
      );

      // Tạo URL tạm thời để tải file
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `student_report_${rollNumber}.${format}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url); // Giải phóng bộ nhớ
    } catch (err) {
      setError('Không thể tải báo cáo. Vui lòng kiểm tra lại.');
      console.error('Error downloading report:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="report-downloader">
      <h2>Tải Báo Cáo Học Sinh</h2>
      
      <div className="form-group">
        <label htmlFor="rollNumber">Mã Số Học Sinh:</label>
        <input
          id="rollNumber"
          type="number"
          value={rollNumber}
          onChange={(e) => setRollNumber(e.target.value ? parseInt(e.target.value) : '')}
          placeholder="Nhập mã số học sinh"
          disabled={loading}
        />
      </div>

      <div className="form-group">
        <label htmlFor="format">Định Dạng Báo Cáo:</label>
        <select
          id="format"
          value={format}
          onChange={(e) => setFormat(e.target.value as ReportFormat)}
          disabled={loading}
        >
          <option value="pdf">PDF</option>
          <option value="xlsx">XLSX</option>
        </select>
      </div>

      {error && <p className="error-message">{error}</p>}

      <button onClick={handleDownload} disabled={loading}>
        {loading ? 'Đang tải...' : 'Tải Báo Cáo'}
      </button>
    </div>
  );
};

export default ReportDownloader;