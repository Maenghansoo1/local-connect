import './Pagination.css'

export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null

  const pages = []
  const start = Math.max(0, page - 2)
  const end   = Math.min(totalPages - 1, page + 2)

  for (let i = start; i <= end; i++) pages.push(i)

  return (
    <nav className="pagination">
      <button
        className="page-btn"
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
      >
        ‹
      </button>

      {start > 0 && (
        <>
          <button className="page-btn" onClick={() => onPageChange(0)}>1</button>
          {start > 1 && <span className="page-ellipsis">…</span>}
        </>
      )}

      {pages.map(p => (
        <button
          key={p}
          className={`page-btn ${p === page ? 'page-btn--active' : ''}`}
          onClick={() => onPageChange(p)}
        >
          {p + 1}
        </button>
      ))}

      {end < totalPages - 1 && (
        <>
          {end < totalPages - 2 && <span className="page-ellipsis">…</span>}
          <button className="page-btn" onClick={() => onPageChange(totalPages - 1)}>
            {totalPages}
          </button>
        </>
      )}

      <button
        className="page-btn"
        disabled={page === totalPages - 1}
        onClick={() => onPageChange(page + 1)}
      >
        ›
      </button>
    </nav>
  )
}
