import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {map, Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseCreate} from '../dto/horse';
import {formatIsoDate} from "../utils/date-helper";


const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient
  ) {
  }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri)
      .pipe(
        map(horses => horses.map(this.fixHorseDate))
      );
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: HorseCreate): Observable<Horse> {
    console.log(horse);
    // Cast the object to any, so that we can circumvent the type checker.
    // We _need_ the date to be a string here, and just passing the object with the
    // “type error” to the HTTP client is unproblematic
    (horse as any).dateOfBirth = formatIsoDate(horse.dateOfBirth);

    return this.http.post<Horse>(
      baseUri,
      horse
    ).pipe(
      map(this.fixHorseDate)
    );
  }

  private fixHorseDate(horse: Horse): Horse {
    // Parse the string to a Date
    horse.dateOfBirth = new Date(horse.dateOfBirth as unknown as string);
    return horse;
  }

  /**
   * Retrieve an existing horse from the system by its ID.
   *
   * @param id the unique identifier of the horse to fetch
   * @return an Observable containing the requested horse details
   */
  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(`${baseUri}/${id}`);
  }

  /**
   * Update an existing horse in the system.
   *
   * @param horse the updated horse data
   * @return an Observable containing the updated horse details
   */
  update(horse: Horse): Observable<Horse> {
    return this.http.put<Horse>(`${baseUri}/${horse.id}`, horse);
  }

  /**
   * Deletes a horse from the system.
   *
   * @param id The unique identifier of the horse to delete
   * @return An Observable for the deletion operation
   */
  delete(id: number | undefined): Observable<void> {
    return this.http.delete<void>(`${baseUri}/${id}`);
  }

  /**
   * Searches for horses based on the given criteria.
   *
   * Each field in the parameters is optional. If a field is not provided or is null,
   * it will not be used as a filter. The search criteria are sent as query parameters to the backend.
   *
   * @param params An object containing the search parameters:
   *               name: Partial or full name of the horse.
   *               description: Partial or full description of the horse.
   *               date_of_birth: Date string to filter horses born on this date.
   *               sex: The gender of the horse ("MALE" or "FEMALE").
   *               ownerName: Partial or full name of the owner.
   * @return An Observable emitting an array of horses that match the search criteria.
   */
  search(params: {
    name?: string | null,
    description?: string | null,
    date_of_birth?: string | null,
    sex?: string | null,
    ownerName?: string | null
  }): Observable<Horse[]> {
    let query = new HttpParams();
    for (const key of Object.keys(params)) {
      const value = params[key as keyof typeof params];
      if (value) {
        query = query.set(key, value);
      }
    }
    return this.http.get<Horse[]>(baseUri, { params: query })
      .pipe(map(horses => horses.map(this.fixHorseDate)));
  }

}
