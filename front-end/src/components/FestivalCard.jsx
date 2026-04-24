import './FestivalCard.css'

const REGION_COLORS = {
  '서울': '#E63946', '부산': '#457B9D', '제주': '#2A9D8F',
  '경기': '#E9C46A', '강원': '#52B788', '대구': '#F4A261',
  '인천': '#A8DADC', '광주': '#9B5DE5', '대전': '#F15BB5',
  '울산': '#00BBF9', '세종': '#00F5D4', '충북': '#FEE440',
  '충남': '#FB5607', '경북': '#FF006E', '경남': '#8338EC',
  '전북': '#3A86FF', '전남': '#06D6A0',
}

function formatDate(str) {
  if (!str || str.length !== 8) return str ?? ''
  const d = new Date(`${str.slice(0,4)}-${str.slice(4,6)}-${str.slice(6,8)}`)
  return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

export default function FestivalCard({ festival }) {
  const { title, region, startDate, endDate, address, imageUrl, tel } = festival
  const color = REGION_COLORS[region] ?? '#6C757D'

  return (
    <article className="festival-card">
      <div className="card-image-wrap">
        {imageUrl
          ? <img src={imageUrl} alt={title} loading="lazy" />
          : <div className="card-image-placeholder" style={{ background: `linear-gradient(135deg, ${color}33, ${color}88)` }}>
              <span>{region}</span>
            </div>
        }
        <span className="region-badge" style={{ background: color }}>{region}</span>
      </div>

      <div className="card-body">
        <h3 className="card-title">{title}</h3>

        <div className="card-meta">
          <span className="meta-icon">📅</span>
          <span>{formatDate(startDate)}
            {endDate && endDate !== startDate && <> &ndash; {formatDate(endDate)}</>}
          </span>
        </div>

        {address && (
          <div className="card-meta">
            <span className="meta-icon">📍</span>
            <span>{address}</span>
          </div>
        )}

        {tel && (
          <div className="card-meta">
            <span className="meta-icon">📞</span>
            <span>{tel}</span>
          </div>
        )}
      </div>
    </article>
  )
}
