const BASE = '/api/events'

export async function fetchFestivals({ region = '', keyword = '', page = 0, size = 12 } = {}) {
  const params = new URLSearchParams({ page, size, sort: 'startDate,asc' })
  if (region)  params.set('region', region)
  if (keyword) params.set('keyword', keyword)

  const res = await fetch(`${BASE}?${params}`)
  if (!res.ok) throw new Error(`Server error: ${res.status}`)
  return res.json()   // Spring Page<EventResponse>
}
