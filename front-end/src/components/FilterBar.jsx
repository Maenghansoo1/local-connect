import { useState } from 'react'
import './FilterBar.css'

const REGIONS = [
  '서울', '부산', '제주', '경기', '강원', '인천',
  '대구', '광주', '대전', '울산', '세종',
  '충북', '충남', '경북', '경남', '전북', '전남',
]

export default function FilterBar({ region, keyword, onRegionChange, onKeywordChange }) {
  const [draft, setDraft] = useState(keyword)

  function handleSubmit(e) {
    e.preventDefault()
    onKeywordChange(draft.trim())
  }

  return (
    <div className="filter-bar">
      <form className="search-form" onSubmit={handleSubmit}>
        <input
          className="search-input"
          type="text"
          placeholder="Search festivals…"
          value={draft}
          onChange={e => setDraft(e.target.value)}
        />
        <button className="search-btn" type="submit">Search</button>
      </form>

      <div className="region-chips">
        <button
          className={`chip ${region === '' ? 'chip--active' : ''}`}
          onClick={() => onRegionChange('')}
        >
          All
        </button>
        {REGIONS.map(r => (
          <button
            key={r}
            className={`chip ${region === r ? 'chip--active' : ''}`}
            onClick={() => onRegionChange(r)}
          >
            {r}
          </button>
        ))}
      </div>
    </div>
  )
}
