import { useState, useEffect, useCallback } from 'react'
import { fetchFestivals } from './api/festivals'
import FestivalCard from './components/FestivalCard'
import FilterBar from './components/FilterBar'
import Pagination from './components/Pagination'
import './App.css'

export default function App() {
  const [festivals, setFestivals]   = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [totalItems, setTotalItems] = useState(0)
  const [page, setPage]             = useState(0)
  const [region, setRegion]         = useState('')
  const [keyword, setKeyword]       = useState('')
  const [loading, setLoading]       = useState(true)
  const [error, setError]           = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await fetchFestivals({ region, keyword, page })
      setFestivals(data.content ?? [])
      setTotalPages(data.totalPages ?? 0)
      setTotalItems(data.totalElements ?? 0)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [region, keyword, page])

  useEffect(() => { load() }, [load])

  function handleRegionChange(r) {
    setRegion(r)
    setKeyword('')
    setPage(0)
  }

  function handleKeywordChange(kw) {
    setKeyword(kw)
    setRegion('')
    setPage(0)
  }

  return (
    <div className="app">
      {/* ── Header ── */}
      <header className="site-header">
        <div className="header-inner">
          <div className="logo">
            <span className="logo-icon">🎉</span>
            <span className="logo-text">Korea Festivals</span>
          </div>
          <p className="header-tagline">Discover authentic Korean festivals across the country</p>
        </div>
      </header>

      {/* ── Main ── */}
      <main className="main-content">
        <FilterBar
          region={region}
          keyword={keyword}
          onRegionChange={handleRegionChange}
          onKeywordChange={handleKeywordChange}
        />

        {/* ── Result info ── */}
        {!loading && !error && (
          <p className="result-count">
            {totalItems > 0
              ? `${totalItems.toLocaleString()} festival${totalItems !== 1 ? 's' : ''} found`
              : 'No festivals found'}
          </p>
        )}

        {/* ── States ── */}
        {loading && (
          <div className="state-box">
            <div className="spinner" />
            <p>Loading festivals…</p>
          </div>
        )}

        {error && (
          <div className="state-box state-box--error">
            <p>⚠️ Could not connect to server.</p>
            <p className="state-sub">Make sure the backend is running on port 8080.</p>
            <button className="retry-btn" onClick={load}>Retry</button>
          </div>
        )}

        {/* ── Grid ── */}
        {!loading && !error && festivals.length > 0 && (
          <section className="festival-grid">
            {festivals.map(f => (
              <FestivalCard key={f.id} festival={f} />
            ))}
          </section>
        )}

        {!loading && !error && festivals.length === 0 && (
          <div className="state-box">
            <p>😔 No festivals match your search.</p>
            <button className="retry-btn" onClick={() => { handleRegionChange(''); handleKeywordChange('') }}>
              Clear filters
            </button>
          </div>
        )}

        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </main>

      <footer className="site-footer">
        <p>Data provided by <strong>Korea Tourism Organization</strong> (한국관광공사)</p>
      </footer>
    </div>
  )
}
