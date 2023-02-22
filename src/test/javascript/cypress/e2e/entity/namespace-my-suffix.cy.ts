import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Namespace e2e test', () => {
  const namespacePageUrl = '/namespace-my-suffix';
  const namespacePageUrlPattern = new RegExp('/namespace-my-suffix(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const namespaceSample = {};

  let namespace;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/namespaces+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/namespaces').as('postEntityRequest');
    cy.intercept('DELETE', '/api/namespaces/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (namespace) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/namespaces/${namespace.id}`,
      }).then(() => {
        namespace = undefined;
      });
    }
  });

  it('Namespaces menu should load Namespaces page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('namespace-my-suffix');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Namespace').should('exist');
    cy.url().should('match', namespacePageUrlPattern);
  });

  describe('Namespace page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(namespacePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Namespace page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/namespace-my-suffix/new$'));
        cy.getEntityCreateUpdateHeading('Namespace');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', namespacePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/namespaces',
          body: namespaceSample,
        }).then(({ body }) => {
          namespace = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/namespaces+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/namespaces?page=0&size=20>; rel="last",<http://localhost/api/namespaces?page=0&size=20>; rel="first"',
              },
              body: [namespace],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(namespacePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Namespace page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('namespace');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', namespacePageUrlPattern);
      });

      it('edit button click should load edit Namespace page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Namespace');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', namespacePageUrlPattern);
      });

      it('edit button click should load edit Namespace page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Namespace');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', namespacePageUrlPattern);
      });

      it('last delete button click should delete instance of Namespace', () => {
        cy.intercept('GET', '/api/namespaces/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('namespace').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', namespacePageUrlPattern);

        namespace = undefined;
      });
    });
  });

  describe('new Namespace page', () => {
    beforeEach(() => {
      cy.visit(`${namespacePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Namespace');
    });

    it('should create an instance of Namespace', () => {
      cy.get(`[data-cy="name"]`).type('connect Kids haptic').should('have.value', 'connect Kids haptic');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        namespace = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', namespacePageUrlPattern);
    });
  });
});
